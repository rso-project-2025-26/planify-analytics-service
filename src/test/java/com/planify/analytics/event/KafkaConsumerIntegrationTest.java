package com.planify.analytics.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planify.analytics.service.AnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EnableAutoConfiguration(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
@DirtiesContext
@EmbeddedKafka(
    partitions = 1,
    topics = {
        "event-created",
        "event-updated",
        "event-deleted",
        "guest-invited",
        "rsvp-accepted",
        "rsvp-declined",
        "guest-checked-in"
    },
    brokerProperties = {
        "listeners=PLAINTEXT://localhost:9092",
        "port=9092"
    }
)
class KafkaConsumerIntegrationTest {
    
    @Autowired
    private KafkaConsumer kafkaConsumer;
    
    @MockBean
    private AnalyticsService analyticsService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Test
    void consumeEventCreated_shouldCallAnalyticsService() throws Exception {
        // Given
        UUID eventId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        String message = String.format(
            "{\"eventId\":\"%s\",\"organizationId\":\"%s\",\"title\":\"Test Event\",\"eventDate\":\"2026-01-15T10:00:00\",\"status\":\"DRAFT\"}",
            eventId, organizationId
        );
        
        // When
        kafkaConsumer.consumeEventCreated(message);
        
        // Then
        verify(analyticsService, timeout(3000)).handleEventCreated(
            eq(eventId),
            eq(organizationId),
            eq("Test Event"),
            any(LocalDateTime.class),
            eq("DRAFT")
        );
    }
    
    @Test
    void consumeEventUpdated_shouldCallAnalyticsService() throws Exception {
        // Given
        UUID eventId = UUID.randomUUID();
        String message = String.format("{\"eventId\":\"%s\"}", eventId);
        
        // When
        kafkaConsumer.consumeEventUpdated(message);
        
        // Then
        verify(analyticsService, timeout(3000)).handleEventUpdated(eq(eventId));
    }
    
    @Test
    void consumeEventDeleted_shouldCallAnalyticsService() throws Exception {
        // Given
        UUID eventId = UUID.randomUUID();
        String message = String.format("{\"eventId\":\"%s\"}", eventId);
        
        // When
        kafkaConsumer.consumeEventDeleted(message);
        
        // Then
        verify(analyticsService, timeout(3000)).handleEventDeleted(eq(eventId));
    }
    
    @Test
    void consumeGuestInvited_shouldCallAnalyticsService() throws Exception {
        // Given
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String message = String.format("{\"eventId\":\"%s\",\"userId\":\"%s\"}", eventId, userId);
        
        // When
        kafkaConsumer.consumeGuestInvited(message);
        
        // Then
        verify(analyticsService, timeout(3000)).handleGuestInvited(eq(eventId), eq(userId));
    }
    
    @Test
    void consumeRsvpAccepted_shouldCallAnalyticsService() throws Exception {
        // Given
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String message = String.format("{\"eventId\":\"%s\",\"userId\":\"%s\"}", eventId, userId);
        
        // When
        kafkaConsumer.consumeRsvpAccepted(message);
        
        // Then
        verify(analyticsService, timeout(3000)).handleRsvpAccepted(eq(eventId), eq(userId));
    }
    
    @Test
    void consumeRsvpDeclined_shouldCallAnalyticsService() throws Exception {
        // Given
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String message = String.format("{\"eventId\":\"%s\",\"userId\":\"%s\"}", eventId, userId);
        
        // When
        kafkaConsumer.consumeRsvpDeclined(message);
        
        // Then
        verify(analyticsService, timeout(3000)).handleRsvpDeclined(eq(eventId), eq(userId));
    }
    
    @Test
    void consumeGuestCheckedIn_shouldCallAnalyticsService() throws Exception {
        // Given
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String message = String.format("{\"eventId\":\"%s\",\"userId\":\"%s\"}", eventId, userId);
        
        // When
        kafkaConsumer.consumeGuestCheckedIn(message);
        
        // Then
        verify(analyticsService, timeout(3000)).handleGuestCheckedIn(eq(eventId), eq(userId));
    }
}
