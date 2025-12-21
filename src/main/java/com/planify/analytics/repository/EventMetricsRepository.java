package com.planify.analytics.repository;

import com.planify.analytics.model.EventMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventMetricsRepository extends JpaRepository<EventMetrics, Long> {
    
    Optional<EventMetrics> findByEventId(Long eventId);
    
    List<EventMetrics> findByOrganizationId(UUID organizationId);
    
    List<EventMetrics> findByEventStatus(String status);
    
    @Query("SELECT e FROM EventMetrics e WHERE e.eventDate >= :startDate AND e.eventDate <= :endDate")
    List<EventMetrics> findByEventDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT e FROM EventMetrics e WHERE e.organizationId = :organizationId AND e.eventDate >= :startDate")
    List<EventMetrics> findUpcomingEventsByOrganization(UUID organizationId, LocalDateTime startDate);
    
    @Query("SELECT COUNT(e) FROM EventMetrics e WHERE e.eventStatus = 'PUBLISHED'")
    Long countActiveEvents();
}