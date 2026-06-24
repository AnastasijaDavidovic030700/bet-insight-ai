package com.betinsight.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "anomalies")
public class Anomaly {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    private String description;

    private String severity;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "daily_report_id", nullable = false)
    private DailyReport dailyReport;

    public Anomaly() {
    }

    public Anomaly(Long id, String type, String description, String severity,
                   LocalDateTime createdAt, Branch branch, DailyReport dailyReport) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.severity = severity;
        this.createdAt = createdAt;
        this.branch = branch;
        this.dailyReport = dailyReport;
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getSeverity() {
        return severity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Branch getBranch() {
        return branch;
    }

    public DailyReport getDailyReport() {
        return dailyReport;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public void setDailyReport(DailyReport dailyReport) {
        this.dailyReport = dailyReport;
    }
}