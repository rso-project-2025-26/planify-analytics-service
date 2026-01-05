package com.planify.analytics.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Planify Analytics Service API",
        version = "1.0.0",
        description = "Microservice for analytics and metrics tracking. Provides event metrics, user activity tracking, and organization analytics via Kafka stream processing.",
        contact = @Contact(
            name = "Planify Analytics Service Repository - Documentation",
            url = "https://github.com/rso-project-2025-26/planify-analytics-service"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8084", description = "Local Development"),
        // @Server(url = "", description = "Production")
    }
)
public class OpenApiConfig {
}
