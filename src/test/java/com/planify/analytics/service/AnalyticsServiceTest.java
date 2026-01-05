package com.planify.analytics.service;

import com.planify.analytics.model.EventMetrics;
import com.planify.analytics.model.SystemMetrics;
import com.planify.analytics.model.UserActivity;
import com.planify.analytics.repository.EventMetricsRepository;
import com.planify.analytics.repository.SystemMetricsRepository;
import com.planify.analytics.repository.UserActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {
    
    @Mock
    private EventMetricsRepository eventMetricsRepository;
    
    @Mock
    private UserActivityRepository userActivityRepository;
    
    @Mock
    private SystemMetricsRepository systemMetricsRepository;
    
    @InjectMocks
    private AnalyticsService analyticsService;
    
    private UUID eventId;
    private UUID organizationId;
    private UUID userId;
    private EventMetrics eventMetrics;
    
    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        organizationId = UUID.randomUUID();
        userId = UUID.randomUUID();
        
        eventMetrics = new EventMetrics();
        eventMetrics.setId(1L);
        eventMetrics.setEventId(eventId);
        eventMetrics.setOrganizationId(organizationId);
        eventMetrics.setEventTitle("Test Event");
        eventMetrics.setEventDate(LocalDateTime.now());
        eventMetrics.setEventStatus("ACTIVE");
        eventMetrics.setTotalInvites(0);
        eventMetrics.setRsvpAccepted(0);
        eventMetrics.setRsvpDeclined(0);
        eventMetrics.setCheckedIn(0);
    }
    
    @Test
    void handleEventCreated_shouldSaveEventMetrics() {
        // Given
        String title = "New Event";
        LocalDateTime eventDate = LocalDateTime.now();
        String status = "DRAFT";
        when(eventMetricsRepository.count()).thenReturn(1L);
        
        // When
        analyticsService.handleEventCreated(eventId, organizationId, title, eventDate, status);
        
        // Then
        verify(eventMetricsRepository).save(any(EventMetrics.class));
        verify(systemMetricsRepository).save(any(SystemMetrics.class));
    }
    
    @Test
    void handleEventUpdated_shouldUpdateExistingMetrics() {
        // Given
        when(eventMetricsRepository.findByEventId(eventId)).thenReturn(Optional.of(eventMetrics));
        
        // When
        analyticsService.handleEventUpdated(eventId);
        
        // Then
        verify(eventMetricsRepository).findByEventId(eventId);
        verify(eventMetricsRepository).save(eventMetrics);
        assertThat(eventMetrics.getUpdatedAt()).isNotNull();
    }
    
    @Test
    void handleEventUpdated_shouldDoNothingWhenEventNotFound() {
        // Given
        when(eventMetricsRepository.findByEventId(eventId)).thenReturn(Optional.empty());
        
        // When
        analyticsService.handleEventUpdated(eventId);
        
        // Then
        verify(eventMetricsRepository).findByEventId(eventId);
        verify(eventMetricsRepository, never()).save(any());
    }
    
    @Test
    void handleEventDeleted_shouldDeleteMetrics() {
        // Given
        when(eventMetricsRepository.findByEventId(eventId)).thenReturn(Optional.of(eventMetrics));
        when(eventMetricsRepository.count()).thenReturn(0L);
        
        // When
        analyticsService.handleEventDeleted(eventId);
        
        // Then
        verify(eventMetricsRepository).delete(eventMetrics);
        verify(systemMetricsRepository).save(any(SystemMetrics.class));
    }
    
    @Test
    void handleGuestInvited_shouldIncrementInvitesAndRecordActivity() {
        // Given
        when(eventMetricsRepository.findByEventId(eventId)).thenReturn(Optional.of(eventMetrics));
        
        // When
        analyticsService.handleGuestInvited(eventId, userId);
        
        // Then
        verify(eventMetricsRepository).save(eventMetrics);
        verify(userActivityRepository).save(any(UserActivity.class));
        assertThat(eventMetrics.getTotalInvites()).isEqualTo(1);
    }
    
    @Test
    void handleRsvpAccepted_shouldIncrementAcceptedCountAndRecordActivity() {
        // Given
        when(eventMetricsRepository.findByEventId(eventId)).thenReturn(Optional.of(eventMetrics));
        when(userActivityRepository.count()).thenReturn(1L);
        
        // When
        analyticsService.handleRsvpAccepted(eventId, userId);
        
        // Then
        verify(eventMetricsRepository).save(eventMetrics);
        verify(userActivityRepository).save(any(UserActivity.class));
        verify(systemMetricsRepository).save(any(SystemMetrics.class));
        assertThat(eventMetrics.getRsvpAccepted()).isEqualTo(1);
    }
    
    @Test
    void handleRsvpDeclined_shouldIncrementDeclinedCountAndRecordActivity() {
        // Given
        when(eventMetricsRepository.findByEventId(eventId)).thenReturn(Optional.of(eventMetrics));
        
        // When
        analyticsService.handleRsvpDeclined(eventId, userId);
        
        // Then
        verify(eventMetricsRepository).save(eventMetrics);
        verify(userActivityRepository).save(any(UserActivity.class));
        assertThat(eventMetrics.getRsvpDeclined()).isEqualTo(1);
    }
    
    @Test
    void handleGuestCheckedIn_shouldIncrementCheckedInCountAndRecordActivity() {
        // Given
        when(eventMetricsRepository.findByEventId(eventId)).thenReturn(Optional.of(eventMetrics));
        
        // When
        analyticsService.handleGuestCheckedIn(eventId, userId);
        
        // Then
        verify(eventMetricsRepository).save(eventMetrics);
        verify(userActivityRepository).save(any(UserActivity.class));
        assertThat(eventMetrics.getCheckedIn()).isEqualTo(1);
    }
    
    @Test
    void getEventMetrics_shouldReturnMetrics() {
        // Given
        when(eventMetricsRepository.findByEventId(eventId)).thenReturn(Optional.of(eventMetrics));
        
        // When
        EventMetrics result = analyticsService.getEventMetrics(eventId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEventId()).isEqualTo(eventId);
        verify(eventMetricsRepository).findByEventId(eventId);
    }
    
    @Test
    void getEventMetrics_shouldThrowExceptionWhenNotFound() {
        // Given
        when(eventMetricsRepository.findByEventId(eventId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> analyticsService.getEventMetrics(eventId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Event metrics not found");
    }
    
    @Test
    void getEventMetricsByOrganization_shouldReturnListOfMetrics() {
        // Given
        List<EventMetrics> metrics = List.of(eventMetrics);
        when(eventMetricsRepository.findByOrganizationId(organizationId)).thenReturn(metrics);
        
        // When
        List<EventMetrics> result = analyticsService.getEventMetricsByOrganization(organizationId);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrganizationId()).isEqualTo(organizationId);
        verify(eventMetricsRepository).findByOrganizationId(organizationId);
    }
    
    @Test
    void getUserActivities_shouldReturnUserActivities() {
        // Given
        UserActivity activity = new UserActivity();
        activity.setUserId(userId);
        activity.setEventId(eventId);
        activity.setActivityType("RSVP_ACCEPTED");
        List<UserActivity> activities = List.of(activity);
        when(userActivityRepository.findByUserId(userId)).thenReturn(activities);
        
        // When
        List<UserActivity> result = analyticsService.getUserActivities(userId);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
        verify(userActivityRepository).findByUserId(userId);
    }
    
    @Test
    void getActiveEventsCount_shouldReturnCount() {
        // Given
        when(eventMetricsRepository.countActiveEvents()).thenReturn(5L);
        
        // When
        Long result = analyticsService.getActiveEventsCount();
        
        // Then
        assertThat(result).isEqualTo(5L);
        verify(eventMetricsRepository).countActiveEvents();
    }
}
