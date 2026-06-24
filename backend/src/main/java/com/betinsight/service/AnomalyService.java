package com.betinsight.service;

import com.betinsight.entity.Anomaly;
import com.betinsight.entity.Branch;
import com.betinsight.entity.DailyReport;
import com.betinsight.repository.AnomalyRepository;
import com.betinsight.repository.DailyReportRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnomalyService {

    private final AnomalyRepository anomalyRepository;
    private final DailyReportRepository dailyReportRepository;

    public AnomalyService(AnomalyRepository anomalyRepository,
                          DailyReportRepository dailyReportRepository) {
        this.anomalyRepository = anomalyRepository;
        this.dailyReportRepository = dailyReportRepository;
    }

    public List<Anomaly> getAllAnomalies() {
        return anomalyRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Anomaly> getAnomaliesByBranchId(Long branchId) {
        return anomalyRepository.findByBranchIdOrderByCreatedAtDesc(branchId);
    }

    public List<Anomaly> generateAnomalies() {
        List<DailyReport> reports = dailyReportRepository.findAll();
        List<Anomaly> createdAnomalies = new ArrayList<>();

        Map<Long, List<DailyReport>> reportsByBranch = groupReportsByBranch(reports);

        for (DailyReport report : reports) {
            if (report.getBranch() == null) {
                continue;
            }

            checkHighPayoutRatio(report, createdAnomalies);
            checkNegativeProfit(report, createdAnomalies);
            checkHighPayoutAmount(report, reportsByBranch, createdAnomalies);
        }

        return createdAnomalies;
    }

    private void checkHighPayoutRatio(DailyReport report, List<Anomaly> createdAnomalies) {
        BigDecimal totalPayments = valueOrZero(report.getTotalPayments());
        BigDecimal totalPayouts = valueOrZero(report.getTotalPayouts());

        if (totalPayments.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        BigDecimal payoutRatio = totalPayouts.divide(totalPayments, 4, RoundingMode.HALF_UP);

        if (payoutRatio.compareTo(new BigDecimal("0.90")) >= 0) {
            createAnomalyIfNotExists(
                    report,
                    "HIGH_PAYOUT_RATIO",
                    "Isplate su veće od 90% ukupnih uplata za ovaj dan.",
                    "HIGH",
                    createdAnomalies
            );
        }
    }

    private void checkNegativeProfit(DailyReport report, List<Anomaly> createdAnomalies) {
        BigDecimal grossProfit = valueOrZero(report.getGrossProfit());

        if (grossProfit.compareTo(BigDecimal.ZERO) < 0) {
            createAnomalyIfNotExists(
                    report,
                    "NEGATIVE_PROFIT",
                    "Poslovnica je ovog dana imala negativan bruto profit.",
                    "CRITICAL",
                    createdAnomalies
            );
        }
    }

    private void checkHighPayoutAmount(DailyReport report,
                                       Map<Long, List<DailyReport>> reportsByBranch,
                                       List<Anomaly> createdAnomalies) {
        Long branchId = report.getBranch().getId();
        List<DailyReport> branchReports = reportsByBranch.get(branchId);

        if (branchReports == null || branchReports.size() < 3) {
            return;
        }

        BigDecimal currentPayouts = valueOrZero(report.getTotalPayouts());
        BigDecimal totalOtherPayouts = BigDecimal.ZERO;
        int otherReportsCount = 0;

        for (DailyReport branchReport : branchReports) {
            if (!branchReport.getId().equals(report.getId())) {
                totalOtherPayouts = totalOtherPayouts.add(valueOrZero(branchReport.getTotalPayouts()));
                otherReportsCount++;
            }
        }

        if (otherReportsCount == 0) {
            return;
        }

        BigDecimal averageOtherPayouts = totalOtherPayouts.divide(
                BigDecimal.valueOf(otherReportsCount),
                2,
                RoundingMode.HALF_UP
        );

        BigDecimal threshold = averageOtherPayouts.multiply(new BigDecimal("1.50"));

        if (currentPayouts.compareTo(threshold) > 0) {
            createAnomalyIfNotExists(
                    report,
                    "HIGH_PAYOUT_AMOUNT",
                    "Isplate su više od 50% veće od proseka ostalih dana za istu poslovnicu.",
                    "HIGH",
                    createdAnomalies
            );
        }
    }

    private void createAnomalyIfNotExists(DailyReport report,
                                          String type,
                                          String description,
                                          String severity,
                                          List<Anomaly> createdAnomalies) {
        boolean alreadyExists = anomalyRepository.existsByDailyReportIdAndType(report.getId(), type);

        if (alreadyExists) {
            return;
        }

        Branch branch = report.getBranch();

        Anomaly anomaly = new Anomaly();
        anomaly.setType(type);
        anomaly.setDescription(description);
        anomaly.setSeverity(severity);
        anomaly.setBranch(branch);
        anomaly.setDailyReport(report);

        Anomaly savedAnomaly = anomalyRepository.save(anomaly);
        createdAnomalies.add(savedAnomaly);
    }

    private Map<Long, List<DailyReport>> groupReportsByBranch(List<DailyReport> reports) {
        Map<Long, List<DailyReport>> reportsByBranch = new HashMap<>();

        for (DailyReport report : reports) {
            if (report.getBranch() == null) {
                continue;
            }

            Long branchId = report.getBranch().getId();

            reportsByBranch
                    .computeIfAbsent(branchId, key -> new ArrayList<>())
                    .add(report);
        }

        return reportsByBranch;
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}