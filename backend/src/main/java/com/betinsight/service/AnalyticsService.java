package com.betinsight.service;

import com.betinsight.dto.DashboardStatsResponse;
import com.betinsight.entity.DailyReport;
import com.betinsight.repository.DailyReportRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private final DailyReportRepository dailyReportRepository;

    public AnalyticsService(DailyReportRepository dailyReportRepository) {
        this.dailyReportRepository = dailyReportRepository;
    }

    public DashboardStatsResponse getDashboardStats() {
        List<DailyReport> reports = dailyReportRepository.findAll();

        BigDecimal totalPayments = BigDecimal.ZERO;
        BigDecimal totalPayouts = BigDecimal.ZERO;
        BigDecimal grossProfit = BigDecimal.ZERO;
        int totalTickets = 0;

        Map<String, BigDecimal> branchProfitMap = new HashMap<>();
        Map<String, BigDecimal> branchPaymentsMap = new HashMap<>();
        Map<String, BigDecimal> branchPayoutsMap = new HashMap<>();

        for (DailyReport report : reports) {
            BigDecimal reportPayments = report.getTotalPayments() != null
                    ? report.getTotalPayments()
                    : BigDecimal.ZERO;

            BigDecimal reportPayouts = report.getTotalPayouts() != null
                    ? report.getTotalPayouts()
                    : BigDecimal.ZERO;

            BigDecimal reportProfit = report.getGrossProfit() != null
                    ? report.getGrossProfit()
                    : reportPayments.subtract(reportPayouts);

            int reportTickets = report.getNumberOfTickets() != null
                    ? report.getNumberOfTickets()
                    : 0;

            totalPayments = totalPayments.add(reportPayments);
            totalPayouts = totalPayouts.add(reportPayouts);
            grossProfit = grossProfit.add(reportProfit);
            totalTickets += reportTickets;

            if (report.getBranch() != null) {
                String branchName = report.getBranch().getName();

                branchProfitMap.put(
                        branchName,
                        branchProfitMap.getOrDefault(branchName, BigDecimal.ZERO).add(reportProfit)
                );

                branchPaymentsMap.put(
                        branchName,
                        branchPaymentsMap.getOrDefault(branchName, BigDecimal.ZERO).add(reportPayments)
                );

                branchPayoutsMap.put(
                        branchName,
                        branchPayoutsMap.getOrDefault(branchName, BigDecimal.ZERO).add(reportPayouts)
                );
            }
        }

        BigDecimal averageTicketAmount = BigDecimal.ZERO;

        if (totalTickets > 0) {
            averageTicketAmount = totalPayments.divide(
                    BigDecimal.valueOf(totalTickets),
                    2,
                    RoundingMode.HALF_UP
            );
        }

        String bestBranchName = findBestBranch(branchProfitMap);
        String highestRiskBranchName = findHighestRiskBranch(branchPaymentsMap, branchPayoutsMap);

        return new DashboardStatsResponse(
                totalPayments,
                totalPayouts,
                grossProfit,
                averageTicketAmount,
                totalTickets,
                reports.size(),
                bestBranchName,
                highestRiskBranchName
        );
    }

    private String findBestBranch(Map<String, BigDecimal> branchProfitMap) {
        String bestBranch = "No data";
        BigDecimal bestProfit = null;

        for (Map.Entry<String, BigDecimal> entry : branchProfitMap.entrySet()) {
            if (bestProfit == null || entry.getValue().compareTo(bestProfit) > 0) {
                bestProfit = entry.getValue();
                bestBranch = entry.getKey();
            }
        }

        return bestBranch;
    }

    private String findHighestRiskBranch(Map<String, BigDecimal> branchPaymentsMap,
                                         Map<String, BigDecimal> branchPayoutsMap) {
        String highestRiskBranch = "No data";
        BigDecimal highestRatio = null;

        for (String branchName : branchPaymentsMap.keySet()) {
            BigDecimal payments = branchPaymentsMap.getOrDefault(branchName, BigDecimal.ZERO);
            BigDecimal payouts = branchPayoutsMap.getOrDefault(branchName, BigDecimal.ZERO);

            if (payments.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal ratio = payouts.divide(payments, 4, RoundingMode.HALF_UP);

                if (highestRatio == null || ratio.compareTo(highestRatio) > 0) {
                    highestRatio = ratio;
                    highestRiskBranch = branchName;
                }
            }
        }

        return highestRiskBranch;
    }
}