package com.recky.demo.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.recky.demo.dao.ActivityLogRepository;
import com.recky.demo.dto.ActivityLogDTO;
import com.recky.demo.dto.ActivityLogStatsDTO;
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
            Optional<User> userOpt = userService.getUserById(userId);

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

    // Some new and untested methods

    public Optional<ActivityLogDTO> getActivityById(String userId, Long id) {
        Optional<ActivityLog> activityLogOpt = activityLogRepository.findById(id);

        // Check if activity exists and belongs to the user
        if (activityLogOpt.isEmpty() || !activityLogOpt.get().getUser().getId().equals(userId)) {
            return Optional.empty();
        }

        return activityLogOpt.map(this::mapToDTO);
    }

    public Page<ActivityLogDTO> getPaginatedLogsByUserId(String userId, Pageable pageable) {
        // Fetch paginated logs from the repository
        Page<ActivityLog> logs = activityLogRepository.findByUserId(userId, pageable);

        // Map the ActivityLog entities to ActivityLogDTO
        Page<ActivityLogDTO> logDTOs = logs.map(this::mapToDTO);

        return logDTOs;
    }

    public Page<ActivityLogDTO> getAdminLogsByUserId(String userId, Pageable pageable) {
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty() || !userOpt.get().getRole().equals(User.Role.ADMIN)) {
            throw new IllegalArgumentException("Unauthorized access: User is not an admin");
        }

        // Fetch paginated admin logs
        Page<ActivityLog> logs = activityLogRepository.findAll(pageable);

        // Map the ActivityLog entities to ActivityLogDTO
        Page<ActivityLogDTO> logDTOs = logs.map(this::mapToDTO);

        return logDTOs;
    }



    public ActivityLogStatsDTO getUserLogStats(String userId) {
        // Validate user exists
        if (userService.getUserById(userId).isEmpty()) {
            throw new IllegalArgumentException("User not found with userId: " + userId);
        }

        // Retrieve logs for the specific user
        List<ActivityLog> userLogs = activityLogRepository.findByUserId(userId);

        // Group logs by action and count
        Map<ActivityLog.Action, Long> actionCounts = userLogs.stream()
            .collect(Collectors.groupingBy(
                ActivityLog::getAction, 
                Collectors.counting()
            ));

        // Calculate total logs
        long totalLogs = userLogs.size();

        // Create and return stats DTO
        return new ActivityLogStatsDTO(
            actionCounts.getOrDefault(ActivityLog.Action.GET, 0L),
            actionCounts.getOrDefault(ActivityLog.Action.CREATE, 0L),
            actionCounts.getOrDefault(ActivityLog.Action.UPDATE, 0L),
            actionCounts.getOrDefault(ActivityLog.Action.DELETE, 0L),
            totalLogs
        );
    }

    public ActivityLogStatsDTO getAllLogsStats() {
        // Retrieve all logs
        List<ActivityLog> allLogs = activityLogRepository.findAll();

        // Group logs by action and count
        Map <ActivityLog.Action, Long> actionCounts = allLogs.stream()
            .collect(Collectors.groupingBy(
                ActivityLog::getAction, 
                Collectors.counting()
            ));

        // Calculate total logs
        long totalLogs = allLogs.size();

        // Create and return stats DTO
        return new ActivityLogStatsDTO(
            actionCounts.getOrDefault(ActivityLog.Action.GET, 0L),
            actionCounts.getOrDefault(ActivityLog.Action.CREATE, 0L),
            actionCounts.getOrDefault(ActivityLog.Action.UPDATE, 0L),
            actionCounts.getOrDefault(ActivityLog.Action.DELETE, 0L),
            totalLogs
        );
    }

}
