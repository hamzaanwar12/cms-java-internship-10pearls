package com.recky.demo.dto;

public class UserStatsDTO {
    private long totalUsers;
    private long adminUsers;
    private long nonAdminUsers;

    public UserStatsDTO(long totalUsers, long adminUsers, long nonAdminUsers) {
        this.totalUsers = totalUsers;
        this.adminUsers = adminUsers;
        this.nonAdminUsers = nonAdminUsers;
    }

    // Getters and setters
    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getAdminUsers() {
        return adminUsers;
    }

    public void setAdminUsers(long adminUsers) {
        this.adminUsers = adminUsers;
    }

    public long getNonAdminUsers() {
        return nonAdminUsers;
    }

    public void setNonAdminUsers(long nonAdminUsers) {
        this.nonAdminUsers = nonAdminUsers;
    }
}