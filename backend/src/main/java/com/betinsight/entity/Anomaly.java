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

    private Integer riskScore;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String recommendedActions;

    private String status = "NEW";

    @Lob
    @Column(columnDefinition = "TEXT")
    private String managerNote;

    private LocalDateTime createdAt;

    private LocalDateTime reviewedAt;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "daily_report_id", nullable = false)
    private DailyReport dailyReport;

    public Anomaly() {
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (status == null) {
            status = "NEW";
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

    public Integer getRiskScore() {
        return riskScore;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getRecommendedActions() {
        return recommendedActions;
    }

    public String getStatus() {
        return status;
    }

    public String getManagerNote() {
        return managerNote;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
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

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public void setRecommendedActions(String recommendedActions) {
        this.recommendedActions = recommendedActions;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setManagerNote(String managerNote) {
        this.managerNote = managerNote;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public void setDailyReport(DailyReport dailyReport) {
        this.dailyReport = dailyReport;
    }
}