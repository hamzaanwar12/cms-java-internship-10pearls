package com.recky.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recky.demo.dao.ActivityLogRepository;
import com.recky.demo.dto.ActivityLogDTO;
import com.recky.demo.model.ActivityLog;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    @Autowired
    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    // Create new ActivityLog entry
    public ActivityLogDTO logActivity(Long userId, String action, Long performedBy, String details) {
        // Convert string action to Action enum
        ActivityLog.Action actionEnum = ActivityLog.Action.valueOf(action.toUpperCase());
        ActivityLog activityLog = new ActivityLog(userId, actionEnum, performedBy, details);
        ActivityLog savedLog = activityLogRepository.save(activityLog);
        return mapToDTO(savedLog);
    }

    // Get logs by userId
    public List<ActivityLogDTO> getLogsByUserId(Long userId) {
        List<ActivityLog> logs = activityLogRepository.findByUserId(userId);
        return logs.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Get logs by performedBy (who performed the action)
    public List<ActivityLogDTO> getLogsByPerformedBy(Long performedBy) {
        List<ActivityLog> logs = activityLogRepository.findByPerformedBy(performedBy);
        return logs.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Get logs by userId and action type
    public List<ActivityLogDTO> getLogsByUserIdAndAction(Long userId, String action) {
        List<ActivityLog> logs = activityLogRepository.findByUserIdAndAction(userId, action);
        return logs.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Convert ActivityLog to ActivityLogDTO
    private ActivityLogDTO mapToDTO(ActivityLog activityLog) {
        return new ActivityLogDTO(
                activityLog.getId(),
                activityLog.getUserId(),
                activityLog.getAction().name(), // Convert Action enum to String
                activityLog.getPerformedBy(),
                activityLog.getTimestamp(), // Assuming timestamp is stored as LocalDateTime
                activityLog.getDetails() // Map details
        );
    }
}
