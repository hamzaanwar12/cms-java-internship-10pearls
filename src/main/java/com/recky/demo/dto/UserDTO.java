package com.recky.demo.dto;

import java.time.LocalDateTime;

public class UserDTO {
    // private Long id;
    private String id;
    private String username;
    private String email;

    private String role;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deactivatedAt;
    private String deactivatedBy;
    private int contactCount;

    // Constructors
    // public UserDTO(String id, String username, String email, String name, String
    // role, String status,
    // LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime
    // deactivatedAt, String deactivatedBy,
    // int contactCount)
    public UserDTO(String id, String username, String email, String role, String status,
            LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deactivatedAt, String deactivatedBy,
            int contactCount) {
        this.id = id;
        this.username = username;
        this.email = email;

        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deactivatedAt = deactivatedAt;
        this.deactivatedBy = deactivatedBy;
        this.contactCount = contactCount;
    }

    // empty constructor:
    public UserDTO() {
        // Initialize fields with default values if necessary
        this.id = null;
        this.username = null;
        this.email = null;
        this.role = null;
        this.status = null;
        this.createdAt = null;
        this.updatedAt = null;
        this.deactivatedAt = null;
        this.deactivatedBy = null;
        this.contactCount = 0;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeactivatedAt() {
        return deactivatedAt;
    }

    public void setDeactivatedAt(LocalDateTime deactivatedAt) {
        this.deactivatedAt = deactivatedAt;
    }

    public String getDeactivatedBy() {
        return deactivatedBy;
    }

    public void setDeactivatedBy(String deactivatedBy) {
        this.deactivatedBy = deactivatedBy;
    }

    public int getContactCount() {
        return contactCount;
    }

    public void setContactCount(int contactCount) {
        this.contactCount = contactCount;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                // ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deactivatedAt=" + deactivatedAt +
                ", deactivatedBy=" + deactivatedBy +
                ", contactCount=" + contactCount +
                '}';
    }
}
