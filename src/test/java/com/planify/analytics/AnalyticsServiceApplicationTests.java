package com.planify.analytics;

import com.planify.analytics.event.KafkaConsumer;
import com.planify.analytics.repository.EventMetricsRepository;
import com.planify.analytics.repository.UserActivityRepository;
import com.planify.analytics.repository.SystemMetricsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class AnalyticsServiceApplicationTests {

    @MockitoBean
    EventMetricsRepository eventMetricsRepository;

    @MockitoBean
    UserActivityRepository userActivityRepository;

    @MockitoBean
    SystemMetricsRepository systemMetricsRepository;

    @MockitoBean
    KafkaConsumer kafkaConsumer;

    @Test
    void contextLoads() {
    }
}
