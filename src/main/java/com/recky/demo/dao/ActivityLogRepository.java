package com.recky.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recky.demo.model.ActivityLog;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByUserId(Long userId);

    List<ActivityLog> findByPerformedBy(Long performedBy);

    List<ActivityLog> findByUserIdAndAction(Long userId, String action);
}
