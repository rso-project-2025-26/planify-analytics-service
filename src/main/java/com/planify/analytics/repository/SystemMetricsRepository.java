package com.planify.analytics.repository;

import com.planify.analytics.model.SystemMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SystemMetricsRepository extends JpaRepository<SystemMetrics, Long> {
    
    List<SystemMetrics> findByMetricName(String metricName);
    
    @Query("SELECT sm FROM SystemMetrics sm WHERE sm.metricName = :metricName AND sm.metricTimestamp >= :startDate ORDER BY sm.metricTimestamp DESC")
    List<SystemMetrics> findRecentMetricsByName(String metricName, LocalDateTime startDate);
    
    @Query("SELECT sm FROM SystemMetrics sm WHERE sm.metricTimestamp >= :startDate AND sm.metricTimestamp <= :endDate")
    List<SystemMetrics> findMetricsBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT sm FROM SystemMetrics sm WHERE sm.metricName = :metricName ORDER BY sm.metricTimestamp DESC")
    List<SystemMetrics> findLatestMetricByName(String metricName);
}