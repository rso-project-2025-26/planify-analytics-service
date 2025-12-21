package com.planify.analytics.controller;

import com.planify.analytics.model.EventMetrics;
import com.planify.analytics.model.SystemMetrics;
import com.planify.analytics.model.UserActivity;
import com.planify.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class GraphQLResolver {
    
    private final AnalyticsService analyticsService;
    
    // Event Metrics Queries
    @QueryMapping
    public EventMetrics eventMetrics(@Argument String eventId) {
        return analyticsService.getEventMetrics(Long.parseLong(eventId));
    }
    
    @QueryMapping
    public List<EventMetrics> eventMetricsByOrganization(@Argument String organizationId) {
        return analyticsService.getEventMetricsByOrganization(UUID.fromString(organizationId));
    }
    
    @QueryMapping
    public Integer activeEventsCount() {
        return analyticsService.getActiveEventsCount().intValue();
    }
    
    // User Activity Queries
    @QueryMapping
    public List<UserActivity> userActivities(@Argument String userId) {
        return analyticsService.getUserActivities(UUID.fromString(userId));
    }
    
    @QueryMapping
    public List<UserActivity> eventActivities(@Argument String eventId) {
        return analyticsService.getEventActivities(Long.parseLong(eventId));
    }
    
    // System Metrics Queries
    @QueryMapping
    public List<SystemMetrics> systemMetrics(@Argument String metricName) {
        return analyticsService.getSystemMetricsByName(metricName);
    }
}