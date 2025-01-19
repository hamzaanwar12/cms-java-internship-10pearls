package com.recky.demo.dto;

public class ActivityLogRequest {
    private String userId;  // Changed to String to match user_id change
    private String action;
    private Long performedBy;
    private String details;

    // Getters and Setters
    public String getUserId() {  // Changed to String
        return userId;
    }

    public void setUserId(String userId) {  // Changed to String
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(Long performedBy) {
        this.performedBy = performedBy;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
