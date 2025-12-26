package com.planify.analytics.controller;

import com.planify.analytics.model.EventMetrics;
import com.planify.analytics.model.UserActivity;
import com.planify.analytics.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Analytics", description = "Analytics and metrics API")
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    @GetMapping("/events/{eventId}")
    @Operation(summary = "Get event metrics by event ID")
    public ResponseEntity<EventMetrics> getEventMetrics(@PathVariable UUID eventId) {
        log.info("Getting metrics for event: {}", eventId);
        return ResponseEntity.ok(analyticsService.getEventMetrics(eventId));
    }
    
    @GetMapping("/organizations/{organizationId}/events")
    @Operation(summary = "Get all event metrics for an organization")
    public ResponseEntity<List<EventMetrics>> getOrganizationEventMetrics(@PathVariable UUID organizationId) {
        log.info("Getting metrics for organization: {}", organizationId);
        return ResponseEntity.ok(analyticsService.getEventMetricsByOrganization(organizationId));
    }
    
    @GetMapping("/users/{userId}/activities")
    @Operation(summary = "Get user activity history")
    public ResponseEntity<List<UserActivity>> getUserActivities(@PathVariable UUID userId) {
        log.info("Getting activities for user: {}", userId);
        return ResponseEntity.ok(analyticsService.getUserActivities(userId));
    }
    
    @GetMapping("/events/{eventId}/activities")
    @Operation(summary = "Get all activities for an event")
    public ResponseEntity<List<UserActivity>> getEventActivities(@PathVariable UUID eventId) {
        log.info("Getting activities for event: {}", eventId);
        return ResponseEntity.ok(analyticsService.getEventActivities(eventId));
    }
    
    @GetMapping("/system/active-events")
    @Operation(summary = "Get count of active events")
    public ResponseEntity<Long> getActiveEventsCount() {
        log.info("Getting active events count");
        return ResponseEntity.ok(analyticsService.getActiveEventsCount());
    }
}