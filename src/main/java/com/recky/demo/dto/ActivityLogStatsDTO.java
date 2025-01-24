package com.recky.demo.dto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.recky.demo.model.ActivityLog;

public class ActivityLogStatsDTO {
    private long getCount;
    private long createCount;
    private long updateCount;
    private long deleteCount;
    private long totalLogs;

    public ActivityLogStatsDTO(long getCount, long createCount, long updateCount, long deleteCount, long totalLogs) {
        this.getCount = getCount;
        this.createCount = createCount;
        this.updateCount = updateCount;
        this.deleteCount = deleteCount;
        this.totalLogs = totalLogs;
    }

    // Getters
    public long getGetCount() {
        return getCount;
    }

    public long getCreateCount() {
        return createCount;
    }

    public long getUpdateCount() {
        return updateCount;
    }

    public long getDeleteCount() {
        return deleteCount;
    }

    public long getTotalLogs() {
        return totalLogs;
    }
}