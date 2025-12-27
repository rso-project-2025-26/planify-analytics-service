package com.planify.analytics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemMetrics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "metric_name", nullable = false, length = 100)
    private String metricName;
    
    @Column(name = "metric_value", nullable = false)
    private Double metricValue;
    
    @Column(name = "metric_timestamp", nullable = false)
    private LocalDateTime metricTimestamp = LocalDateTime.now();
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum MetricName {
        TOTAL_EVENTS,
        TOTAL_USERS,
        ACTIVE_EVENTS,
        TOTAL_RSVPS,
        AVERAGE_RESPONSE_TIME,
        SYSTEM_UPTIME
    }
}