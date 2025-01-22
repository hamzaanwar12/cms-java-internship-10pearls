package com.recky.demo.dao;

import java.util.List;

// import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.recky.demo.model.ActivityLog;
import com.recky.demo.model.ActivityLog.Action; // Import the enum

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    // Update userId parameter to String
    List<ActivityLog> findByUserId(String userId); 

    // List<ActivityLog> findByPerformedBy(Long performedBy);

    // List<ActivityLog> findByUserIdAndAction(Long userId, String action);
    // Update userId parameter to String
    List<ActivityLog> findByUserIdAndAction(String userId, Action action); // Changed Long to String

    // some new and untested
    // Page<ActivityLog> findByUserId(String userId, PageRequest pageRequest);

    Page<ActivityLog> findByUserId(String userId, Pageable pageable);

    // Find all activity logs for admins (this could be for all users, or further restricted by other conditions)
    Page<ActivityLog> findAll(Pageable pageable);
}
