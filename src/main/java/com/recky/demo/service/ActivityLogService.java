package com.recky.demo.service;

import java.util.List;
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

            ActivityLog activityLog = new ActivityLog(user, actionEnum, details);
            ActivityLog savedLog = activityLogRepository.save(activityLog);

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
