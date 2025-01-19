package com.recky.demo.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "activity_logs")
public class ActivityLog {
    private static final Logger logger = LoggerFactory.getLogger(ActivityLog.class);

    public enum Action {
        CREATE, UPDATE, DELETE, LOGIN, LOGOUT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Action action;

    @Column(name = "performed_by", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long performedBy;

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime timestamp;

    @Column(name = "details", columnDefinition = "TEXT DEFAULT NULL")
    private String details;

    // Default constructor required by JPA
    public ActivityLog() {
    }

    // Constructor for new activity log creation
    public ActivityLog(User user, Action action, Long performedBy, String details) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        logger.info("Creating activity log - User ID: {}, Action: {}, Performed By: {}",
                user.getId(), action, performedBy);

        this.user = user;
        this.action = action;
        this.performedBy = performedBy;
        this.details = details;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        this.user = user;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
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

    protected void setTimestamp(LocalDateTime timestamp) {
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
        return "ActivityLog{" +
                "id=" + id +
                ", user=" + (user != null ? user.getId() : null) +
                ", action=" + action +
                ", performedBy=" + performedBy +
                ", timestamp=" + timestamp +
                ", details='" + details + '\'' +
                '}';
    }
}
