package com.betinsight.service;

import com.betinsight.dto.AnomalyReviewRequest;
import com.betinsight.entity.Anomaly;
import com.betinsight.entity.Branch;
import com.betinsight.entity.DailyReport;
import com.betinsight.repository.AnomalyRepository;
import com.betinsight.repository.DailyReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
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

    @Transactional
    public List<Anomaly> regenerateAnomaliesForReport(DailyReport report) {
        anomalyRepository.deleteByDailyReportId(report.getId());

        List<Anomaly> createdAnomalies = new ArrayList<>();
        List<DailyReport> reports = dailyReportRepository.findAll();

        Map<Long, List<DailyReport>> reportsByBranch = groupReportsByBranch(reports);

        if (report.getBranch() == null) {
            return createdAnomalies;
        }

        checkHighPayoutRatio(report, createdAnomalies);
        checkNegativeProfit(report, createdAnomalies);
        checkHighPayoutAmount(report, reportsByBranch, createdAnomalies);

        return createdAnomalies;
    }

    public Anomaly updateReview(Long id, AnomalyReviewRequest request) {
        Anomaly anomaly = anomalyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anomaly not found with id: " + id));

        anomaly.setStatus(request.getStatus());
        anomaly.setManagerNote(request.getManagerNote());
        anomaly.setReviewedAt(LocalDateTime.now());

        return anomalyRepository.save(anomaly);
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
                    82,
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
                    95,
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
                    88,
                    createdAnomalies
            );
        }
    }

    private void createAnomalyIfNotExists(DailyReport report,
                                          String type,
                                          String description,
                                          String severity,
                                          Integer riskScore,
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
        anomaly.setRiskScore(riskScore);
        anomaly.setBranch(branch);
        anomaly.setDailyReport(report);
        anomaly.setStatus("NEW");
        anomaly.setExplanation(buildExplanation(type, report));
        anomaly.setRecommendedActions(buildRecommendedActions(type));

        Anomaly savedAnomaly = anomalyRepository.save(anomaly);
        createdAnomalies.add(savedAnomaly);
    }

    private String buildExplanation(String type, DailyReport report) {
        String branchName = report.getBranch() != null ? report.getBranch().getName() : "Unknown branch";

        BigDecimal payments = valueOrZero(report.getTotalPayments());
        BigDecimal payouts = valueOrZero(report.getTotalPayouts());
        BigDecimal profit = valueOrZero(report.getGrossProfit());

        if ("HIGH_PAYOUT_RATIO".equals(type)) {
            BigDecimal ratio = BigDecimal.ZERO;

            if (payments.compareTo(BigDecimal.ZERO) > 0) {
                ratio = payouts
                        .divide(payments, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
            }

            return "AI Investigation: The report for " + branchName + " on " + report.getReportDate()
                    + " was flagged because payouts reached " + ratio.setScale(2, RoundingMode.HALF_UP)
                    + "% of total payments. This is above the internal 90% risk threshold. "
                    + "The system recommends reviewing whether this was caused by regular customer wins, "
                    + "a specific event, or an unusual payout pattern.";
        }

        if ("HIGH_PAYOUT_AMOUNT".equals(type)) {
            return "AI Investigation: The report for " + branchName + " on " + report.getReportDate()
                    + " was flagged because the payout amount was significantly higher than the branch's usual payout level. "
                    + "This may indicate a normal high-win day, but it should be compared with previous reports, "
                    + "largest tickets, and time periods with increased payouts.";
        }

        if ("NEGATIVE_PROFIT".equals(type)) {
            return "AI Investigation: The report for " + branchName + " on " + report.getReportDate()
                    + " shows negative gross profit of " + profit + ". "
                    + "This means payouts exceeded payments for the selected day. "
                    + "The case should be reviewed to determine whether the loss was expected, seasonal, event-driven, or operationally unusual.";
        }

        return "AI Investigation: This report was flagged because it contains an unusual business pattern that requires management review.";
    }

    private String buildRecommendedActions(String type) {
        if ("HIGH_PAYOUT_RATIO".equals(type)) {
            return "1. Review the largest payouts for this date.\n"
                    + "2. Compare payout ratio with the previous 7 business days.\n"
                    + "3. Check whether payouts are connected to a specific sport, match, event, or time period.\n"
                    + "4. Mark the case as REVIEWED if the pattern is explained.";
        }

        if ("HIGH_PAYOUT_AMOUNT".equals(type)) {
            return "1. Compare this payout amount with the branch average.\n"
                    + "2. Review the top winning tickets for this day.\n"
                    + "3. Check whether this is a repeated pattern in the same branch.\n"
                    + "4. Escalate if high payouts continue for multiple days.";
        }

        if ("NEGATIVE_PROFIT".equals(type)) {
            return "1. Check whether total payouts exceeded payments due to a small number of large wins.\n"
                    + "2. Compare with the same weekday in previous weeks.\n"
                    + "3. Review operational notes for this date.\n"
                    + "4. Mark as RESOLVED after business justification is added.";
        }

        return "1. Review the report manually.\n2. Add manager note.\n3. Update case status.";
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