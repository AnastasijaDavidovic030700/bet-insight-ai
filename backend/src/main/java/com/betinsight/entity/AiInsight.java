package com.betinsight.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_insights")
public class AiInsight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate periodFrom;

    private LocalDate periodTo;

    private String riskLevel;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String executiveSummary;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String keyFindings;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String recommendedActions;

    private LocalDateTime createdAt;

    public AiInsight() {
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

    public LocalDate getPeriodFrom() {
        return periodFrom;
    }

    public LocalDate getPeriodTo() {
        return periodTo;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getExecutiveSummary() {
        return executiveSummary;
    }

    public String getKeyFindings() {
        return keyFindings;
    }

    public String getRecommendedActions() {
        return recommendedActions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPeriodFrom(LocalDate periodFrom) {
        this.periodFrom = periodFrom;
    }

    public void setPeriodTo(LocalDate periodTo) {
        this.periodTo = periodTo;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public void setExecutiveSummary(String executiveSummary) {
        this.executiveSummary = executiveSummary;
    }

    public void setKeyFindings(String keyFindings) {
        this.keyFindings = keyFindings;
    }

    public void setRecommendedActions(String recommendedActions) {
        this.recommendedActions = recommendedActions;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}