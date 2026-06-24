package com.betinsight.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyReportRequest {

    private Long branchId;
    private LocalDate reportDate;
    private Integer numberOfTickets;
    private BigDecimal totalPayments;
    private BigDecimal totalPayouts;
    private String note;

    public DailyReportRequest() {
    }

    public Long getBranchId() {
        return branchId;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public Integer getNumberOfTickets() {
        return numberOfTickets;
    }

    public BigDecimal getTotalPayments() {
        return totalPayments;
    }

    public BigDecimal getTotalPayouts() {
        return totalPayouts;
    }

    public String getNote() {
        return note;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public void setNumberOfTickets(Integer numberOfTickets) {
        this.numberOfTickets = numberOfTickets;
    }

    public void setTotalPayments(BigDecimal totalPayments) {
        this.totalPayments = totalPayments;
    }

    public void setTotalPayouts(BigDecimal totalPayouts) {
        this.totalPayouts = totalPayouts;
    }

    public void setNote(String note) {
        this.note = note;
    }
}