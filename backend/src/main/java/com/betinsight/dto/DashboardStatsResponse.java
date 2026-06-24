package com.betinsight.dto;

import java.math.BigDecimal;

public class DashboardStatsResponse {

    private BigDecimal totalPayments;
    private BigDecimal totalPayouts;
    private BigDecimal grossProfit;
    private BigDecimal averageTicketAmount;
    private Integer totalTickets;
    private Integer numberOfReports;
    private String bestBranchName;
    private String highestRiskBranchName;

    public DashboardStatsResponse() {
    }

    public DashboardStatsResponse(BigDecimal totalPayments, BigDecimal totalPayouts,
                                  BigDecimal grossProfit, BigDecimal averageTicketAmount,
                                  Integer totalTickets, Integer numberOfReports,
                                  String bestBranchName, String highestRiskBranchName) {
        this.totalPayments = totalPayments;
        this.totalPayouts = totalPayouts;
        this.grossProfit = grossProfit;
        this.averageTicketAmount = averageTicketAmount;
        this.totalTickets = totalTickets;
        this.numberOfReports = numberOfReports;
        this.bestBranchName = bestBranchName;
        this.highestRiskBranchName = highestRiskBranchName;
    }

    public BigDecimal getTotalPayments() {
        return totalPayments;
    }

    public void setTotalPayments(BigDecimal totalPayments) {
        this.totalPayments = totalPayments;
    }

    public BigDecimal getTotalPayouts() {
        return totalPayouts;
    }

    public void setTotalPayouts(BigDecimal totalPayouts) {
        this.totalPayouts = totalPayouts;
    }

    public BigDecimal getGrossProfit() {
        return grossProfit;
    }

    public void setGrossProfit(BigDecimal grossProfit) {
        this.grossProfit = grossProfit;
    }

    public BigDecimal getAverageTicketAmount() {
        return averageTicketAmount;
    }

    public void setAverageTicketAmount(BigDecimal averageTicketAmount) {
        this.averageTicketAmount = averageTicketAmount;
    }

    public Integer getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(Integer totalTickets) {
        this.totalTickets = totalTickets;
    }

    public Integer getNumberOfReports() {
        return numberOfReports;
    }

    public void setNumberOfReports(Integer numberOfReports) {
        this.numberOfReports = numberOfReports;
    }

    public String getBestBranchName() {
        return bestBranchName;
    }

    public void setBestBranchName(String bestBranchName) {
        this.bestBranchName = bestBranchName;
    }

    public String getHighestRiskBranchName() {
        return highestRiskBranchName;
    }

    public void setHighestRiskBranchName(String highestRiskBranchName) {
        this.highestRiskBranchName = highestRiskBranchName;
    }
}