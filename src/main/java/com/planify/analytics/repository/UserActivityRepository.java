package com.planify.analytics.repository;

import com.planify.analytics.model.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    
    List<UserActivity> findByUserId(UUID userId);
    
    List<UserActivity> findByEventId(Long eventId);
    
    List<UserActivity> findByActivityType(String activityType);
    
    @Query("SELECT ua FROM UserActivity ua WHERE ua.userId = :userId AND ua.activityTimestamp >= :startDate")
    List<UserActivity> findRecentActivityByUser(UUID userId, LocalDateTime startDate);
    
    @Query("SELECT ua FROM UserActivity ua WHERE ua.eventId = :eventId AND ua.activityType = :activityType")
    List<UserActivity> findByEventIdAndActivityType(Long eventId, String activityType);
    
    @Query("SELECT COUNT(ua) FROM UserActivity ua WHERE ua.activityTimestamp >= :startDate")
    Long countRecentActivities(LocalDateTime startDate);
}