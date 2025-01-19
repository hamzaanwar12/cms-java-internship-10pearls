package com.recky.demo.dto;

import java.time.LocalDateTime;

public class ActivityLogDTO {
    private Long id;
    private String userId;  // Changed to String to match user_id change
    private String action; // Storing action as a string (ENUM Action converted to String)
    private Long performedBy;
    private LocalDateTime timestamp;
    private String details;

    // Constructors
    public ActivityLogDTO(Long id, String userId, String action, Long performedBy, LocalDateTime timestamp,
            String details) {
        this.id = id;
        this.userId = userId;
        this.action = action;
        this.performedBy = performedBy;
        this.timestamp = timestamp;
        this.details = details;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "ActivityLogDTO{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", action='" + action + '\'' +
                ", performedBy=" + performedBy +
                ", timestamp=" + timestamp +
                ", details='" + details + '\'' +
                '}';
    }
}
