package com.betinsight.dto;

public class AnomalyReviewRequest {

    private String status;
    private String managerNote;

    public AnomalyReviewRequest() {
    }

    public String getStatus() {
        return status;
    }

    public String getManagerNote() {
        return managerNote;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setManagerNote(String managerNote) {
        this.managerNote = managerNote;
    }
}