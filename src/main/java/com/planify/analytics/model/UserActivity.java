package com.planify.analytics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_activity")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActivity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(name = "event_id", nullable = false)
    private Long eventId;
    
    @Column(name = "activity_type", nullable = false, length = 50)
    private String activityType;
    
    @Column(name = "activity_timestamp", nullable = false)
    private LocalDateTime activityTimestamp = LocalDateTime.now();
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum ActivityType {
        RSVP_ACCEPTED,
        RSVP_DECLINED,
        RSVP_MAYBE,
        CHECKED_IN,
        EVENT_VIEWED,
        INVITATION_SENT
    }
}