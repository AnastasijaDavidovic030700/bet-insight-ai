package com.betinsight.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "daily_reports")
public class DailyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate reportDate;

    private Integer numberOfTickets;

    private BigDecimal totalPayments;

    private BigDecimal totalPayouts;

    private BigDecimal grossProfit;

    private BigDecimal averageTicketAmount;

    private String note;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    public DailyReport() {
    }

    public DailyReport(Long id, LocalDate reportDate, Integer numberOfTickets,
                       BigDecimal totalPayments, BigDecimal totalPayouts,
                       BigDecimal grossProfit, BigDecimal averageTicketAmount,
                       String note, Branch branch) {
        this.id = id;
        this.reportDate = reportDate;
        this.numberOfTickets = numberOfTickets;
        this.totalPayments = totalPayments;
        this.totalPayouts = totalPayouts;
        this.grossProfit = grossProfit;
        this.averageTicketAmount = averageTicketAmount;
        this.note = note;
        this.branch = branch;
    }

    public Long getId() {
        return id;
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

    public BigDecimal getGrossProfit() {
        return grossProfit;
    }

    public BigDecimal getAverageTicketAmount() {
        return averageTicketAmount;
    }

    public String getNote() {
        return note;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setGrossProfit(BigDecimal grossProfit) {
        this.grossProfit = grossProfit;
    }

    public void setAverageTicketAmount(BigDecimal averageTicketAmount) {
        this.averageTicketAmount = averageTicketAmount;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }
}