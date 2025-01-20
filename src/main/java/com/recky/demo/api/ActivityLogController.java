package com.recky.demo.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.recky.demo.dto.ActivityLogDTO;
import com.recky.demo.dto.ActivityLogRequest;
import com.recky.demo.service.ActivityLogService;
import com.recky.demo.util.ApiResponse;

@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @Autowired
    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ActivityLogDTO>> logActivity(@RequestBody ActivityLogRequest request) {
        try {
            ActivityLogDTO activityLogDTO = activityLogService.logActivity(
                    request.getUserId(),
                    request.getAction(),
                    request.getDetails());
            ApiResponse<ActivityLogDTO> response = new ApiResponse<>(
                    HttpStatus.CREATED.value(), "success", "Activity logged successfully", activityLogDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            ApiResponse<ActivityLogDTO> response = new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), "error", "An error occurred while logging activity", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ActivityLogDTO>>> getLogsByUserId(@PathVariable String userId) {
        try {
            List<ActivityLogDTO> logs = activityLogService.getLogsByUserId(userId);
            if (logs.isEmpty()) {
                ApiResponse<List<ActivityLogDTO>> response = new ApiResponse<>(
                        HttpStatus.NOT_FOUND.value(), "error", "No logs found for the given user", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            ApiResponse<List<ActivityLogDTO>> response = new ApiResponse<>(
                    HttpStatus.OK.value(), "success", "Logs retrieved successfully", logs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<ActivityLogDTO>> response = new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), "error", "An error occurred while retrieving logs", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/user/{userId}/action/{action}")
    public ResponseEntity<ApiResponse<List<ActivityLogDTO>>> getLogsByUserIdAndAction(@PathVariable String userId,
            @PathVariable String action) {
        try {
            List<ActivityLogDTO> logs = activityLogService.getLogsByUserIdAndAction(userId, action);
            if (logs.isEmpty()) {
                ApiResponse<List<ActivityLogDTO>> response = new ApiResponse<>(
                        HttpStatus.NOT_FOUND.value(), "error", "No logs found for the given user and action", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            ApiResponse<List<ActivityLogDTO>> response = new ApiResponse<>(
                    HttpStatus.OK.value(), "success", "Logs retrieved successfully", logs);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ApiResponse<List<ActivityLogDTO>> response = new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.value(), "error", "Invalid action type: " + action, null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            ApiResponse<List<ActivityLogDTO>> response = new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), "error", "An error occurred while retrieving logs", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
