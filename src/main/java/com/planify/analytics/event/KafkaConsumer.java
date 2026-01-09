package com.planify.analytics.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.planify.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
    
    private final AnalyticsService analyticsService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @KafkaListener(topics = "event-created", groupId = "${spring.application.name}")
    public void consumeEventCreated(String message) {
        log.info("Consumed event-created: {}", message);
        
        try {
            JsonNode json = objectMapper.readTree(message);
            UUID eventId = UUID.fromString(json.get("eventId").asText());
            UUID organizationId = UUID.fromString(json.get("organizationId").asText());
            String title = json.get("title").asText();
            String eventDate = json.get("eventDate").asText();
            String status = json.has("status") ? json.get("status").asText() : "DRAFT";
            
            analyticsService.handleEventCreated(eventId, organizationId, title, LocalDateTime.parse(eventDate), status);
        } catch (Exception e) {
            log.error("Error processing event-created: {}", e.getMessage(), e);
        }
    }
    
    @KafkaListener(topics = "event-updated", groupId = "${spring.application.name}")
    public void consumeEventUpdated(String message) {
        log.info("Consumed event-updated: {}", message);
        
        try {
            JsonNode json = objectMapper.readTree(message);
            UUID eventId = UUID.fromString(json.get("eventId").asText());
            
            analyticsService.handleEventUpdated(eventId);
        } catch (Exception e) {
            log.error("Error processing event-updated: {}", e.getMessage(), e);
        }
    }
    
    @KafkaListener(topics = "event-deleted", groupId = "${spring.application.name}")
    public void consumeEventDeleted(String message) {
        log.info("Consumed event-deleted: {}", message);
        
        try {
            JsonNode json = objectMapper.readTree(message);
            UUID eventId = UUID.fromString(json.get("eventId").asText());
            
            analyticsService.handleEventDeleted(eventId);
        } catch (Exception e) {
            log.error("Error processing event-deleted: {}", e.getMessage(), e);
        }
    }
    
    @KafkaListener(topics = "guest-invited", groupId = "${spring.application.name}")
    public void consumeGuestInvited(String message) {
        log.info("Consumed guest-invited: {}", message);
        
        try {
            JsonNode json = objectMapper.readTree(message);
            UUID eventId = UUID.fromString(json.get("eventId").asText());
            UUID userId = UUID.fromString(json.get("userId").asText());
            
            analyticsService.handleGuestInvited(eventId, userId);
        } catch (Exception e) {
            log.error("Error processing guest-invited: {}", e.getMessage(), e);
        }
    }
    
    @KafkaListener(topics = "rsvp-accepted", groupId = "${spring.application.name}")
    public void consumeRsvpAccepted(String message) {
        log.info("Consumed rsvp-accepted: {}", message);
        
        try {
            JsonNode json = objectMapper.readTree(message);
            UUID eventId = UUID.fromString(json.get("eventId").asText());
            UUID userId = UUID.fromString(json.get("userId").asText());
            
            analyticsService.handleRsvpAccepted(eventId, userId);
        } catch (Exception e) {
            log.error("Error processing rsvp-accepted: {}", e.getMessage(), e);
        }
    }
    
    @KafkaListener(topics = "rsvp-declined", groupId = "${spring.application.name}")
    public void consumeRsvpDeclined(String message) {
        log.info("Consumed rsvp-declined: {}", message);
        
        try {
            JsonNode json = objectMapper.readTree(message);
            UUID eventId = UUID.fromString(json.get("eventId").asText());
            UUID userId = UUID.fromString(json.get("userId").asText());
            
            analyticsService.handleRsvpDeclined(eventId, userId);
        } catch (Exception e) {
            log.error("Error processing rsvp-declined: {}", e.getMessage(), e);
        }
    }
    
    @KafkaListener(topics = "guest-checked-in", groupId = "${spring.application.name}")
    public void consumeGuestCheckedIn(String message) {
        log.info("Consumed guest-checked-in: {}", message);
        
        try {
            JsonNode json = objectMapper.readTree(message);
            UUID eventId = UUID.fromString(json.get("eventId").asText());
            UUID userId = UUID.fromString(json.get("userId").asText());
            
            analyticsService.handleGuestCheckedIn(eventId, userId);
        } catch (Exception e) {
            log.error("Error processing guest-checked-in: {}", e.getMessage(), e);
        }
    }
    
    @KafkaListener(topics = "event-published", groupId = "${spring.application.name}")
    public void consumeEventPublished(String message) {
        log.info("Consumed event-published: {}", message);
        
        try {
            JsonNode json = objectMapper.readTree(message);
            UUID eventId = UUID.fromString(json.get("eventId").asText());
            
            analyticsService.handleEventPublished(eventId);
        } catch (Exception e) {
            log.error("Error processing event-published: {}", e.getMessage(), e);
        }
    }
}