package com.planify.analytics.service;

import com.planify.analytics.model.EventMetrics;
import com.planify.analytics.model.SystemMetrics;
import com.planify.analytics.model.UserActivity;
import com.planify.analytics.repository.EventMetricsRepository;
import com.planify.analytics.repository.SystemMetricsRepository;
import com.planify.analytics.repository.UserActivityRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {
    
    private final EventMetricsRepository eventMetricsRepository;
    private final UserActivityRepository userActivityRepository;
    private final SystemMetricsRepository systemMetricsRepository;
    
    // Event Handlers
    @Transactional
    @Retry(name = "analyticsDatabase")
    @Bulkhead(name = "analyticsDatabase")
    @CircuitBreaker(name = "analyticsDatabase", fallbackMethod = "handleEventCreatedFallback")
    public void handleEventCreated(UUID eventId, UUID organizationId, String title, LocalDateTime eventDate, String status) {
        EventMetrics metrics = new EventMetrics();
        metrics.setEventId(eventId);
        metrics.setOrganizationId(organizationId);
        metrics.setEventTitle(title);
        metrics.setEventDate(eventDate);
        metrics.setEventStatus(status);
        
        eventMetricsRepository.save(metrics);
        log.info("Created event metrics for event: {}", eventId);
        
        // Update system metrics
        updateSystemMetric("TOTAL_EVENTS", (double) eventMetricsRepository.count());
    }
    
    private void handleEventCreatedFallback(UUID eventId, UUID organizationId, String title, LocalDateTime eventDate, String status, Exception ex) {
        log.error("Failed to record event metrics for event {}. Error: {}", eventId, ex.getMessage());
    }
    
    @Transactional
    @Retry(name = "analyticsDatabase")
    @CircuitBreaker(name = "analyticsDatabase", fallbackMethod = "handleEventUpdatedFallback")
    public void handleEventUpdated(UUID eventId) {
        eventMetricsRepository.findByEventId(eventId).ifPresent(metrics -> {
            metrics.setUpdatedAt(LocalDateTime.now());
            eventMetricsRepository.save(metrics);
            log.info("Updated event metrics for event: {}", eventId);
        });
    }
    
    private void handleEventUpdatedFallback(UUID eventId, Exception ex) {
        log.error("Failed to update event metrics for event {}. Error: {}", eventId, ex.getMessage());
    }
    
    @Transactional
    public void handleEventDeleted(UUID eventId) {
        eventMetricsRepository.findByEventId(eventId).ifPresent(metrics -> {
            eventMetricsRepository.delete(metrics);
            log.info("Deleted event metrics for event: {}", eventId);
        });
        
        // Update system metrics
        updateSystemMetric("TOTAL_EVENTS", (double) eventMetricsRepository.count());
    }
    
    @Transactional
    public void handleGuestInvited(UUID eventId, UUID userId) {
        eventMetricsRepository.findByEventId(eventId).ifPresent(metrics -> {
            metrics.setTotalInvites(metrics.getTotalInvites() + 1);
            eventMetricsRepository.save(metrics);
            log.info("Incremented total invites for event: {}", eventId);
        });
        
        // Record user activity
        recordUserActivity(userId, eventId, UserActivity.ActivityType.INVITATION_SENT.name());
    }
    
    @Transactional
    @Retry(name = "analyticsDatabase")
    @Bulkhead(name = "analyticsDatabase")
    @CircuitBreaker(name = "analyticsDatabase", fallbackMethod = "handleRsvpAcceptedFallback")
    public void handleRsvpAccepted(UUID eventId, UUID userId) {
        eventMetricsRepository.findByEventId(eventId).ifPresent(metrics -> {
            metrics.setRsvpAccepted(metrics.getRsvpAccepted() + 1);
            eventMetricsRepository.save(metrics);
            log.info("Incremented RSVP accepted for event: {}", eventId);
        });
        
        // Record user activity
        recordUserActivity(userId, eventId, UserActivity.ActivityType.RSVP_ACCEPTED.name());
        
        // Update system metrics
        updateSystemMetric("TOTAL_RSVPS", (double) userActivityRepository.count());
    }
    
    private void handleRsvpAcceptedFallback(UUID eventId, UUID userId, Exception ex) {
        log.error("Failed to record RSVP accepted for event {}. Error: {}", eventId, ex.getMessage());
    }
    
    @Transactional
    public void handleRsvpDeclined(UUID eventId, UUID userId) {
        eventMetricsRepository.findByEventId(eventId).ifPresent(metrics -> {
            metrics.setRsvpDeclined(metrics.getRsvpDeclined() + 1);
            eventMetricsRepository.save(metrics);
            log.info("Incremented RSVP declined for event: {}", eventId);
        });
        
        // Record user activity
        recordUserActivity(userId, eventId, UserActivity.ActivityType.RSVP_DECLINED.name());
    }
    
    @Transactional
    public void handleGuestCheckedIn(UUID eventId, UUID userId) {
        eventMetricsRepository.findByEventId(eventId).ifPresent(metrics -> {
            metrics.setCheckedIn(metrics.getCheckedIn() + 1);
            eventMetricsRepository.save(metrics);
            log.info("Incremented checked-in count for event: {}", eventId);
        });
        
        // Record user activity
        recordUserActivity(userId, eventId, UserActivity.ActivityType.CHECKED_IN.name());
    }
    
    // Query Methods for GraphQL
    public EventMetrics getEventMetrics(UUID eventId) {
        return eventMetricsRepository.findByEventId(eventId)
                .orElseThrow(() -> new RuntimeException("Event metrics not found for event: " + eventId));
    }
    
    public List<EventMetrics> getEventMetricsByOrganization(UUID organizationId) {
        return eventMetricsRepository.findByOrganizationId(organizationId);
    }
    
    public List<UserActivity> getUserActivities(UUID userId) {
        return userActivityRepository.findByUserId(userId);
    }
    
    public List<UserActivity> getEventActivities(UUID eventId) {
        return userActivityRepository.findByEventId(eventId);
    }
    
    public List<SystemMetrics> getSystemMetricsByName(String metricName) {
        return systemMetricsRepository.findByMetricName(metricName);
    }
    
    public Long getActiveEventsCount() {
        return eventMetricsRepository.countActiveEvents();
    }
    
    // Helper Methods
    private void recordUserActivity(UUID userId, UUID eventId, String activityType) {
        UserActivity activity = new UserActivity();
        activity.setUserId(userId);
        activity.setEventId(eventId);
        activity.setActivityType(activityType);
        activity.setActivityTimestamp(LocalDateTime.now());
        
        userActivityRepository.save(activity);
        log.info("Recorded user activity: {} for user: {} in event: {}", activityType, userId, eventId);
    }
    
    private void updateSystemMetric(String metricName, Double value) {
        SystemMetrics metric = new SystemMetrics();
        metric.setMetricName(metricName);
        metric.setMetricValue(value);
        metric.setMetricTimestamp(LocalDateTime.now());
        
        systemMetricsRepository.save(metric);
        log.info("Updated system metric: {} = {}", metricName, value);
    }
}