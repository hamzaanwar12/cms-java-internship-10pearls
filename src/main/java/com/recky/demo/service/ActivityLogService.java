package com.recky.demo.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recky.demo.dao.ActivityLogRepository;
import com.recky.demo.dto.ActivityLogDTO;
import com.recky.demo.model.ActivityLog;
import com.recky.demo.model.ActivityLog.Action;
import com.recky.demo.model.User;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(ActivityLogService.class);

    @Autowired
    public ActivityLogService(ActivityLogRepository activityLogRepository, UserService userService) {
        this.activityLogRepository = activityLogRepository;
        this.userService = userService;
    }

    public ActivityLogDTO logActivity(String userId, String action, String details) {
        try {
            if (userId == null) {
                throw new IllegalArgumentException("userId cannot be null");
            }

            // Retrieve the user using Optional and handle absence
            Optional <User> userOpt = userService.getUserById(userId);

            if (userOpt.isEmpty()) {
                logger.error("User not found with userId: {}", userId);
                // throw new IllegalArgumentException("User not found with userId: " + userId);
            }
            User user = userOpt.get();
            logger.info("User found with userId: {}", userId);

            // Validate and parse action
            ActivityLog.Action actionEnum;
            try {
                actionEnum = ActivityLog.Action.valueOf(action.toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.error("Invalid action provided: {}", action, e);
                throw new IllegalArgumentException("Invalid action provided: " + action, e);
            }

            // Create and save activity log
            ActivityLog activityLog = new ActivityLog(user, actionEnum, details);
            ActivityLog savedLog = activityLogRepository.save(activityLog);

            // Map to DTO and return
            return mapToDTO(savedLog);
        } catch (Exception e) {
            logger.error("Error logging activity for userId: {}, action: {}, details: {}", userId, action, details, e);
            throw new RuntimeException("An error occurred while logging the activity", e);
        }
    }

    public List<ActivityLogDTO> getLogsByUserId(String userId) {
        List<ActivityLog> logs = activityLogRepository.findByUserId(userId);
        return logs.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<ActivityLogDTO> getLogsByUserIdAndAction(String userId, String action) {
        try {
            Action actionEnum = Action.valueOf(action.toUpperCase());
            List<ActivityLog> logs = activityLogRepository.findByUserIdAndAction(userId, actionEnum);
            return logs.stream().map(this::mapToDTO).collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid action type: " + action);
        }
    }

    private ActivityLogDTO mapToDTO(ActivityLog activityLog) {
        return new ActivityLogDTO(
                activityLog.getId(),
                activityLog.getUser().getId(),
                activityLog.getAction().name(),
                activityLog.getTimestamp(),
                activityLog.getDetails());
    }
}
