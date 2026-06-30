package com.betinsight.service;

import com.betinsight.dto.AiInsightRequest;
import com.betinsight.entity.AiInsight;
import com.betinsight.entity.Anomaly;
import com.betinsight.entity.DailyReport;
import com.betinsight.repository.AiInsightRepository;
import com.betinsight.repository.AnomalyRepository;
import com.betinsight.repository.DailyReportRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class AiInsightService {

    private final AiInsightRepository aiInsightRepository;
    private final DailyReportRepository dailyReportRepository;
    private final AnomalyRepository anomalyRepository;

    public AiInsightService(AiInsightRepository aiInsightRepository,
                            DailyReportRepository dailyReportRepository,
                            AnomalyRepository anomalyRepository) {
        this.aiInsightRepository = aiInsightRepository;
        this.dailyReportRepository = dailyReportRepository;
        this.anomalyRepository = anomalyRepository;
    }

    public List<AiInsight> getAllInsights() {
        return aiInsightRepository.findAllByOrderByCreatedAtDesc();
    }

    public AiInsight generateInsight(AiInsightRequest request) {
        List<DailyReport> reports = dailyReportRepository.findByReportDateBetweenOrderByReportDateAsc(
                request.getPeriodFrom(),
                request.getPeriodTo()
        );

        List<Anomaly> allAnomalies = anomalyRepository.findAllByOrderByCreatedAtDesc();

        int anomaliesInPeriod = 0;
        int criticalAnomalies = 0;
        int highAnomalies = 0;

        for (Anomaly anomaly : allAnomalies) {
            if (anomaly.getDailyReport() == null || anomaly.getDailyReport().getReportDate() == null) {
                continue;
            }

            boolean inPeriod = !anomaly.getDailyReport().getReportDate().isBefore(request.getPeriodFrom())
                    && !anomaly.getDailyReport().getReportDate().isAfter(request.getPeriodTo());

            if (inPeriod) {
                anomaliesInPeriod++;

                if ("CRITICAL".equalsIgnoreCase(anomaly.getSeverity())) {
                    criticalAnomalies++;
                }

                if ("HIGH".equalsIgnoreCase(anomaly.getSeverity())) {
                    highAnomalies++;
                }
            }
        }

        BigDecimal totalPayments = BigDecimal.ZERO;
        BigDecimal totalPayouts = BigDecimal.ZERO;
        BigDecimal grossProfit = BigDecimal.ZERO;
        int totalTickets = 0;

        for (DailyReport report : reports) {
            totalPayments = totalPayments.add(valueOrZero(report.getTotalPayments()));
            totalPayouts = totalPayouts.add(valueOrZero(report.getTotalPayouts()));
            grossProfit = grossProfit.add(valueOrZero(report.getGrossProfit()));

            if (report.getNumberOfTickets() != null) {
                totalTickets += report.getNumberOfTickets();
            }
        }

        BigDecimal payoutRatio = BigDecimal.ZERO;

        if (totalPayments.compareTo(BigDecimal.ZERO) > 0) {
            payoutRatio = totalPayouts
                    .divide(totalPayments, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }

        String riskLevel = calculateRiskLevel(payoutRatio, anomaliesInPeriod, criticalAnomalies);

        AiInsight insight = new AiInsight();
        insight.setPeriodFrom(request.getPeriodFrom());
        insight.setPeriodTo(request.getPeriodTo());
        insight.setRiskLevel(riskLevel);
        insight.setExecutiveSummary(buildExecutiveSummary(
                reports.size(),
                totalPayments,
                totalPayouts,
                grossProfit,
                payoutRatio,
                anomaliesInPeriod,
                riskLevel
        ));
        insight.setKeyFindings(buildKeyFindings(
                totalTickets,
                totalPayments,
                totalPayouts,
                grossProfit,
                payoutRatio,
                anomaliesInPeriod,
                highAnomalies,
                criticalAnomalies
        ));
        insight.setRecommendedActions(buildRecommendedActions(riskLevel, anomaliesInPeriod, payoutRatio));

        return aiInsightRepository.save(insight);
    }

    private String calculateRiskLevel(BigDecimal payoutRatio, int anomaliesInPeriod, int criticalAnomalies) {
        if (criticalAnomalies > 0) {
            return "CRITICAL";
        }

        if (payoutRatio.compareTo(new BigDecimal("90")) >= 0 || anomaliesInPeriod >= 3) {
            return "HIGH";
        }

        if (payoutRatio.compareTo(new BigDecimal("75")) >= 0 || anomaliesInPeriod > 0) {
            return "MEDIUM";
        }

        return "LOW";
    }

    private String buildExecutiveSummary(int numberOfReports,
                                         BigDecimal totalPayments,
                                         BigDecimal totalPayouts,
                                         BigDecimal grossProfit,
                                         BigDecimal payoutRatio,
                                         int anomaliesInPeriod,
                                         String riskLevel) {
        if (numberOfReports == 0) {
            return "No daily reports were found for the selected period. The system cannot generate a reliable business insight without operational data.";
        }

        return "AI Executive Summary: In the selected period, the system analyzed "
                + numberOfReports + " daily business reports. Total payments were "
                + totalPayments + " RSD, while total payouts were " + totalPayouts
                + " RSD. Gross profit for the period was " + grossProfit
                + " RSD. The overall payout ratio was "
                + payoutRatio.setScale(2, RoundingMode.HALF_UP)
                + "%. The system detected " + anomaliesInPeriod
                + " anomaly case(s), resulting in an overall risk level of " + riskLevel + ".";
    }

    private String buildKeyFindings(int totalTickets,
                                    BigDecimal totalPayments,
                                    BigDecimal totalPayouts,
                                    BigDecimal grossProfit,
                                    BigDecimal payoutRatio,
                                    int anomaliesInPeriod,
                                    int highAnomalies,
                                    int criticalAnomalies) {
        return "1. Total number of tickets in the selected period: " + totalTickets + ".\n"
                + "2. Total payments: " + totalPayments + " RSD.\n"
                + "3. Total payouts: " + totalPayouts + " RSD.\n"
                + "4. Gross profit: " + grossProfit + " RSD.\n"
                + "5. Payout ratio: " + payoutRatio.setScale(2, RoundingMode.HALF_UP) + "%.\n"
                + "6. Detected anomalies: " + anomaliesInPeriod + ".\n"
                + "7. High severity anomalies: " + highAnomalies + ".\n"
                + "8. Critical severity anomalies: " + criticalAnomalies + ".";
    }

    private String buildRecommendedActions(String riskLevel, int anomaliesInPeriod, BigDecimal payoutRatio) {
        if ("CRITICAL".equals(riskLevel)) {
            return "1. Immediately review all critical anomaly cases in the Investigation Center.\n"
                    + "2. Check whether payouts exceeded payments in any branch.\n"
                    + "3. Add manager notes for every unresolved case.\n"
                    + "4. Escalate repeated negative-profit patterns to senior management.";
        }

        if ("HIGH".equals(riskLevel)) {
            return "1. Review all high-risk anomaly cases for the selected period.\n"
                    + "2. Compare payout ratio with previous business periods.\n"
                    + "3. Identify branches with repeated high payout behavior.\n"
                    + "4. Mark explained cases as REVIEWED or RESOLVED.";
        }

        if ("MEDIUM".equals(riskLevel)) {
            return "1. Monitor branches with unusual payout ratios.\n"
                    + "2. Review anomaly cases during regular management control.\n"
                    + "3. Compare this period with the same weekday pattern from previous weeks.";
        }

        return "1. No urgent action is required.\n"
                + "2. Continue monitoring daily reports.\n"
                + "3. Keep anomaly detection enabled for early warning signals.";
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}