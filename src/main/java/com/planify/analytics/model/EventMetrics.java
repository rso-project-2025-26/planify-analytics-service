package com.planify.analytics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "event_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventMetrics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "event_id", nullable = false, unique = true)
    private Long eventId;
    
    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;
    
    @Column(name = "event_title", nullable = false)
    private String eventTitle;
    
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;
    
    @Column(name = "event_status")
    private String eventStatus;
    
    // Metrics
    @Column(name = "total_invites")
    private Integer totalInvites = 0;
    
    @Column(name = "rsvp_accepted")
    private Integer rsvpAccepted = 0;
    
    @Column(name = "rsvp_declined")
    private Integer rsvpDeclined = 0;
    
    @Column(name = "rsvp_maybe")
    private Integer rsvpMaybe = 0;
    
    @Column(name = "checked_in")
    private Integer checkedIn = 0;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}