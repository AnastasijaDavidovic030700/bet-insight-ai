package com.betinsight.dto;

import java.time.LocalDate;

public class AiInsightRequest {

    private LocalDate periodFrom;
    private LocalDate periodTo;

    public AiInsightRequest() {
    }

    public LocalDate getPeriodFrom() {
        return periodFrom;
    }

    public void setPeriodFrom(LocalDate periodFrom) {
        this.periodFrom = periodFrom;
    }

    public LocalDate getPeriodTo() {
        return periodTo;
    }

    public void setPeriodTo(LocalDate periodTo) {
        this.periodTo = periodTo;
    }
}