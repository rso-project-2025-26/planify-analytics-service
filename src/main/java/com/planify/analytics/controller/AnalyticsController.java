package com.planify.analytics.controller;

import com.planify.analytics.model.EventMetrics;
import com.planify.analytics.model.UserActivity;
import com.planify.analytics.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "Analytics", description = "Analytics and metrics endpoints for events and user activity")
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    @GetMapping("/events/{eventId}")
    @Operation(
        summary = "Get event metrics",
        description = "Returns comprehensive metrics for a specific event including views, RSVPs, attendance rates, and engagement statistics."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved event metrics",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventMetrics.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content)
    })
    public ResponseEntity<EventMetrics> getEventMetrics(
        @Parameter(required = true)
        @PathVariable UUID eventId) {
        log.info("Getting metrics for event: {}", eventId);
        return ResponseEntity.ok(analyticsService.getEventMetrics(eventId));
    }
    
    @GetMapping("/organizations/{organizationId}/events")
    @Operation(
        summary = "Get organization event metrics",
        description = "Returns aggregated metrics for all events belonging to the specified organization, sorted by most recent events first."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved organization metrics",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventMetrics.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content)
    })
    public ResponseEntity<List<EventMetrics>> getOrganizationEventMetrics(
        @Parameter(required = true)
        @PathVariable UUID organizationId) {
        log.info("Getting metrics for organization: {}", organizationId);
        return ResponseEntity.ok(analyticsService.getEventMetricsByOrganization(organizationId));
    }
    
    @GetMapping("/users/{userId}/activities")
    @Operation(
        summary = "Get user activity history",
        description = "Returns complete activity history for a specific user across all events, including views, RSVPs, and attendance records."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user activities",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserActivity.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content)
    })
    public ResponseEntity<List<UserActivity>> getUserActivities(
            @Parameter(required = true)
            @PathVariable UUID userId) {
        log.info("Getting activities for user: {}", userId);
        return ResponseEntity.ok(analyticsService.getUserActivities(userId));
    }
    
    @GetMapping("/events/{eventId}/activities")
    @Operation(
        summary = "Get event activity logs",
        description = "Returns all user activity logs associated with a specific event, including timestamps and activity types."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved event activities",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserActivity.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content)
    })
    public ResponseEntity<List<UserActivity>> getEventActivities(
            @Parameter(required = true)
            @PathVariable UUID eventId) {
        log.info("Getting activities for event: {}", eventId);
        return ResponseEntity.ok(analyticsService.getEventActivities(eventId));
    }
    
    @GetMapping("/system/active-events")
    @Operation(
        summary = "Get active events count",
        description = "Returns the total number of currently active events across the entire system. Useful for dashboard statistics."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved count",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content)
    })
    public ResponseEntity<Long> getActiveEventsCount() {
        log.info("Getting active events count");
        return ResponseEntity.ok(analyticsService.getActiveEventsCount());
    }
}