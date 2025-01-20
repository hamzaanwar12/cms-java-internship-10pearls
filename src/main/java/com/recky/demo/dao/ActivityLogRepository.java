package com.recky.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recky.demo.model.ActivityLog;
import com.recky.demo.model.ActivityLog.Action; // Import the enum

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    // Update userId parameter to String
    List<ActivityLog> findByUserId(String userId);  // Changed Long to String

    // List<ActivityLog> findByPerformedBy(Long performedBy);

    // List<ActivityLog> findByUserIdAndAction(Long userId, String action);
    // Update userId parameter to String
    List<ActivityLog> findByUserIdAndAction(String userId, Action action);  // Changed Long to String
}
