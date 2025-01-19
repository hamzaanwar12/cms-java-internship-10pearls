package com.recky.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recky.demo.dao.ActivityLogRepository;
import com.recky.demo.dto.ActivityLogDTO;
import com.recky.demo.model.ActivityLog; // Import the enum
import com.recky.demo.model.ActivityLog.Action;
import com.recky.demo.model.User;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final UserService userService; // Assuming you have a service to fetch User

    private static final Logger logger = LoggerFactory.getLogger(ActivityLogService.class);

    @Autowired
    public ActivityLogService(ActivityLogRepository activityLogRepository, UserService userService) {
        this.activityLogRepository = activityLogRepository;
        this.userService = userService;
    }

    public ActivityLogDTO logActivity(String userId, String action, Long performedBy, String details) {
        try {
            if (userId == null) {
                throw new IllegalArgumentException("userId cannot be null");
            }

            User user = userService.getUserByIdOrThrow(userId);
            if (user == null) {
                logger.error("User not found with userId: {}", userId);
                throw new IllegalArgumentException("User not found with userId: " + userId);
            }
            logger.info("User found with userId: {}", userId);

            ActivityLog.Action actionEnum;
            try {
                actionEnum = ActivityLog.Action.valueOf(action.toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.error("Invalid action provided: {}", action, e);
                throw new IllegalArgumentException("Invalid action provided: " + action, e);
            }

            ActivityLog activityLog = new ActivityLog(user, actionEnum, performedBy, details);
            ActivityLog savedLog = activityLogRepository.save(activityLog);

            return mapToDTO(savedLog);
        } catch (Exception e) {
            logger.error("Error logging activity for userId: {}, action: {}, details: {}", userId, action, details, e);
            throw new RuntimeException("An error occurred while logging the activity", e);
        }
    }

    // Get logs by userId
    public List<ActivityLogDTO> getLogsByUserId(String userId) {
        List<ActivityLog> logs = activityLogRepository.findByUserId(userId);
        return logs.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Get logs by performedBy (who performed the action)
    public List<ActivityLogDTO> getLogsByPerformedBy(Long performedBy) {
        List<ActivityLog> logs = activityLogRepository.findByPerformedBy(performedBy);
        return logs.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Get logs by userId and action type
    // public List<ActivityLogDTO> getLogsByUserIdAndAction(String userId, String
    // action) {
    // List<ActivityLog> logs = activityLogRepository.findByUserIdAndAction(userId,
    // action);
    // return logs.stream().map(this::mapToDTO).collect(Collectors.toList());
    // }

    // public List<ActivityLogDTO> getLogsByUserIdAndAction(String userId, String
    // action) {
    // try {
    // Action actionEnum = Action.valueOf(action.toUpperCase());
    // List<ActivityLog> logs = activityLogRepository.findByUserIdAndAction(userId,
    // actionEnum);
    // return logs.stream().map(this::mapToDTO).collect(Collectors.toList());
    // } catch (IllegalArgumentException e) {
    // throw new IllegalArgumentException("Invalid action type: " + action);
    // }
    // }

    public List<ActivityLogDTO> getLogsByUserIdAndAction(String userId, String action) {
        try {
            Action actionEnum = Action.valueOf(action.toUpperCase());
            List<ActivityLog> logs = activityLogRepository.findByUserIdAndAction(userId,
                    actionEnum);
            return logs.stream().map(this::mapToDTO).collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid action type: " + action);
        }
    }

    // Convert ActivityLog to ActivityLogDTO
    private ActivityLogDTO mapToDTO(ActivityLog activityLog) {
        return new ActivityLogDTO(
                activityLog.getId(),
                activityLog.getUser().getId(), // Get userId from User object
                activityLog.getAction().name(),
                activityLog.getPerformedBy(),
                activityLog.getTimestamp(),
                activityLog.getDetails());
    }
}
