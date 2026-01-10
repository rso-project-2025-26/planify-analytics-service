# Analytics Service

Microservice for collecting, analyzing, and reporting metrics on events, user activities, and system performance in the Planify platform. Provides both RESTful and GraphQL APIs for querying analytics data, consumes events from all microservices via Kafka, and integrates with Prometheus and Grafana for visualization.

## Technologies

### Backend Framework & Language
- **Java 21** - Programming language
- **Spring Boot 3.5.7** - Application framework
- **Spring Data JPA** - Database access
- **Hibernate** - ORM framework
- **Lombok** - Boilerplate code reduction

### Database
- **PostgreSQL** - Database
- **Flyway** - Database migrations
- **HikariCP** - Connection pooling

### API Technologies
- **Spring GraphQL** - GraphQL API
- **Spring Web** - REST API
- **SpringDoc OpenAPI 3** - REST API documentation

### Messaging System
- **Apache Kafka** - Event streaming platform
- **Spring Kafka** - Kafka integration

### Monitoring & Health
- **Spring Boot Actuator** - Health checks and metrics
- **Micrometer Prometheus** - Metrics export
- **Resilience4j** - Circuit breakers, retry, rate limiting, bulkheads

### Containerization
- **Docker** - Application containerization
- **Kubernetes/Helm** - Orchestration (Helm charts included)

## System Integrations

- **Kafka**: Consumes events from all microservices (event-manager, guest-service, booking-service, user-service) to collect metrics and track user activities.
- **PostgreSQL**: Stores all metrics data via Hibernate/JPA with Flyway migrations in the `analytics` schema.
- **Prometheus**: Exports application and custom business metrics for monitoring.
- **Grafana**: Visualizes collected metrics through dashboards (configured separately in infrastructure).
- **event-manager-service**: Receives event lifecycle events (created, updated, deleted).
- **guest-service**: Receives RSVP events (accepted, declined, maybe) and check-in events.
- **booking-service**: Receives booking and payment events.
- **user-service**: May query user data via internal APIs for enhanced analytics.

## API Endpoints

The service provides both REST and GraphQL APIs for querying analytics data.

### REST API (`/api/analytics`)

- `GET /api/analytics/events/{eventId}` — Get detailed metrics for a specific event
- `GET /api/analytics/organizations/{organizationId}/events` — Get metrics for all events in an organization
- `GET /api/analytics/users/{userId}/activities` — Get activity history for a specific user
- `GET /api/analytics/events/{eventId}/activities` — Get all user activities for a specific event
- `GET /api/analytics/system/active-events` — Get count of currently active events

### GraphQL API (`/graphql`)

**Event Metrics Queries:**
- `eventMetrics(eventId: ID!)` — Get metrics for a specific event
- `eventMetricsByOrganization(organizationId: ID!)` — Get metrics for all events in an organization
- `activeEventsCount` — Get count of active events

**User Activity Queries:**
- `userActivities(userId: ID!)` — Get activity history for a specific user
- `eventActivities(eventId: ID!)` — Get all activities for a specific event

**System Metrics Queries:**
- `systemMetrics(metricName: String!)` — Get system-level metrics by name

### Minimal curl examples (REST API)

```bash
# Get event metrics
curl "http://localhost:8084/api/analytics/events/550e8400-e29b-41d4-a716-446655440000"

# Get organization event metrics
curl "http://localhost:8084/api/analytics/organizations/880e8400-e29b-41d4-a716-446655440003/events"

# Get user activities
curl "http://localhost:8084/api/analytics/users/990e8400-e29b-41d4-a716-446655440004/activities"

# Get active events count
curl "http://localhost:8084/api/analytics/system/active-events"
```

### GraphQL Examples

**Query event metrics:**
```graphql
query {
  eventMetrics(eventId: "550e8400-e29b-41d4-a716-446655440000") {
    id
    eventTitle
    eventDate
    totalInvites
    rsvpAccepted
    rsvpDeclined
    checkedIn
  }
}
```

**Query user activities:**
```graphql
query {
  userActivities(userId: "990e8400-e29b-41d4-a716-446655440004") {
    id
    eventId
    activityType
    activityTimestamp
  }
}
```

**Query system metrics:**
```graphql
query {
  systemMetrics(metricName: "TOTAL_EVENTS") {
    id
    metricName
    metricValue
    metricTimestamp
  }
}
```

## Database Structure

The service uses PostgreSQL with the following core entities in the `analytics` schema:

### Event Metrics

Aggregated metrics for each event. Contains:

- `id` (BIGSERIAL, PK)
- `event_id` (UUID, unique) - Reference to event in event-manager-service
- `organization_id` (UUID) - Reference to organization in user-service
- `event_title` (VARCHAR) - Event title (denormalized)
- `event_date` (TIMESTAMP) - Event date (denormalized)
- `event_status` (VARCHAR) - Event status (DRAFT, PUBLISHED, CANCELLED, COMPLETED)
- `total_invites` (INT) - Total number of invitations sent
- `rsvp_accepted` (INT) - Number of accepted RSVPs
- `rsvp_declined` (INT) - Number of declined RSVPs
- `rsvp_maybe` (INT) - Number of maybe RSVPs
- `checked_in` (INT) - Number of attendees who checked in
- `created_at` (TIMESTAMP) - Record creation timestamp
- `updated_at` (TIMESTAMP) - Record last update timestamp

**Indexes:**
- `idx_event_metrics_event_id` on `event_id`
- `idx_event_metrics_organization_id` on `organization_id`
- `idx_event_metrics_status` on `event_status`
- `idx_event_metrics_date` on `event_date`

### User Activity

Individual user activity records. Contains:

- `id` (BIGSERIAL, PK)
- `user_id` (UUID) - Reference to user in user-service
- `event_id` (UUID) - Reference to event in event-manager-service
- `activity_type` (VARCHAR) - Type of activity: `RSVP_ACCEPTED`, `RSVP_DECLINED`, `RSVP_MAYBE`, `CHECKED_IN`, `EVENT_VIEWED`, `INVITATION_SENT`
- `activity_timestamp` (TIMESTAMP) - When the activity occurred
- `created_at` (TIMESTAMP) - Record creation timestamp

**Indexes:**
- `idx_user_activity_user_id` on `user_id`
- `idx_user_activity_event_id` on `event_id`
- `idx_user_activity_type` on `activity_type`
- `idx_user_activity_timestamp` on `activity_timestamp`

### System Metrics

System-wide aggregate metrics. Contains:

- `id` (BIGSERIAL, PK)
- `metric_name` (VARCHAR) - Metric identifier: `TOTAL_EVENTS`, `TOTAL_USERS`, `ACTIVE_EVENTS`, `TOTAL_RSVPS`, `AVERAGE_RESPONSE_TIME`, `SYSTEM_UPTIME`
- `metric_value` (DOUBLE) - Numeric value of the metric
- `metric_timestamp` (TIMESTAMP) - When the metric was recorded
- `created_at` (TIMESTAMP) - Record creation timestamp

**Indexes:**
- `idx_system_metrics_name` on `metric_name`
- `idx_system_metrics_timestamp` on `metric_timestamp`

**Relationships**: All entity references use UUIDs for cross-service lookups without foreign key constraints. The service denormalizes some data (event title, date) for performance. Database schema is versioned via Flyway migrations in `src/main/resources/db/migration/`.

## Installation and Setup

### Prerequisites

- Java 21 or newer
- Maven 3.6+
- Docker and Docker Compose
- Git

### Infrastructure Setup

This service requires PostgreSQL and Kafka to run. These dependencies are provided via Docker containers in the main Planify repository.

Clone and setup the infrastructure:

```bash
# Clone the main Planify repository
git clone https://github.com/rso-project-2025-26/planify.git
cd planify

# Follow the setup instructions in the main repository README
# This will start all required infrastructure services (PostgreSQL, Kafka, Prometheus, Grafana)
```

Refer to the main Planify repository (https://github.com/rso-project-2025-26/planify) documentation for detailed infrastructure setup instructions.

### Configuration

The application uses a single `application.yaml` configuration file located in `src/main/resources/`.

Important environment variables:

```
SERVER_PORT=8084
DB_URL=jdbc:postgresql://localhost:5432/planify
SPRING_DATASOURCE_USERNAME=planify
SPRING_DATASOURCE_PASSWORD=planify
DB_SCHEMA=analytics
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
KAFKA_CONSUMER_GROUP=analytics-service
GRAPHQL_GRAPHIQL_ENABLED=true
```

### Local Run

```bash
# Build project
mvn clean package

# Run application
mvn spring-boot:run
```

### Using Makefile

```bash
# Build project
make build

# Docker build
make docker-build

# Docker run
make docker-run

# Tests
make test
```

### Docker Run

```bash
# Build Docker image
docker build -t planify/analytics-service:0.0.1 .

# Run container
docker run -p 8084:8084 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/planify \
  -e KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9092 \
  planify/analytics-service:0.0.1
```

### Kubernetes/Helm Deployment

```bash
# Install with Helm
helm install analytics-service ./helm/analytics

# Install with specific environment values
helm install analytics-service ./helm/analytics -f ./helm/analytics/values-dev.yaml

# Upgrade
helm upgrade analytics-service ./helm/analytics

# Uninstall
helm uninstall analytics-service
```

### Flyway Migrations

Migrations are located in `src/main/resources/db/migration/`:

- `V1__init.sql` - Initial schema with event_metrics, user_activity, and system_metrics tables

Manual migration run:

```bash
mvn flyway:migrate
```

## Health Check & Monitoring

### Actuator Endpoints

- **GET** `/actuator/health` — Health check endpoint
- **GET** `/actuator/health/liveness` — Liveness probe
- **GET** `/actuator/health/readiness` — Readiness probe
- **GET** `/actuator/prometheus` — Prometheus metrics
- **GET** `/actuator/info` — Application information
- **GET** `/actuator/metrics` — Application metrics

### API Documentation

**REST API** - Swagger UI available at:
```
http://localhost:8084/swagger-ui.html
```

OpenAPI specification:
```
http://localhost:8084/api-docs
```

**GraphQL API** - GraphiQL interface available at:
```
http://localhost:8084/graphiql
```

GraphQL endpoint:
```
http://localhost:8084/graphql
```

GraphQL schema:
```
http://localhost:8084/graphql/schema
```

## Kafka Events

### Events Consumed

The service listens to the following Kafka topics to collect analytics data:

**event-created** — Published by event-manager-service when a new event is created

Consumed payload:
```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440000",
  "organizationId": "880e8400-e29b-41d4-a716-446655440003",
  "title": "Annual Conference 2024",
  "eventDate": "2024-12-15T10:00:00Z",
  "status": "DRAFT"
}
```

Action: Creates new event_metrics record with initial counters set to 0

**event-updated** — Published by event-manager-service when event details change

Consumed payload:
```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2024-12-24T10:00:00Z"
}
```

Action: Updates event_metrics record timestamp

**event-deleted** — Published by event-manager-service when event is deleted

Consumed payload:
```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440000"
}
```

Action: Marks event_metrics as deleted or archives the record

**guest-invited** — Published by event-manager-service when a guest is invited

Consumed payload:
```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "990e8400-e29b-41d4-a716-446655440004",
  "organizationId": "880e8400-e29b-41d4-a716-446655440003"
}
```

Action: Increments `total_invites` counter and records `INVITATION_SENT` user activity

**rsvp-accepted** — Published by guest-service when a guest accepts an invitation

Consumed payload:
```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "990e8400-e29b-41d4-a716-446655440004",
  "wasAccepted": false
}
```

Action: Increments `rsvp_accepted` counter and records `RSVP_ACCEPTED` user activity

**rsvp-declined** — Published by guest-service when a guest declines an invitation

Consumed payload:
```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "990e8400-e29b-41d4-a716-446655440004",
  "wasAccepted": true
}
```

Action: Increments `rsvp_declined` counter (decrements `rsvp_accepted` if `wasAccepted` is true) and records `RSVP_DECLINED` user activity

**guest-checked-in** — Published when a guest checks in at the event

Consumed payload:
```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "990e8400-e29b-41d4-a716-446655440004"
}
```

Action: Increments `checked_in` counter and records `CHECKED_IN` user activity

### Events Published

The service currently does not publish events to Kafka. It is a pure consumer service focused on collecting and aggregating metrics from other services.

## Prometheus Integration

The service exports custom business metrics to Prometheus in addition to standard Spring Boot metrics:

**Custom Metrics Exported:**
- `planify_events_total` - Total number of events in the system
- `planify_events_active` - Number of currently active events
- `planify_rsvp_accepted_total` - Total RSVP acceptances
- `planify_rsvp_declined_total` - Total RSVP declines
- `planify_checkins_total` - Total check-ins across all events

**Standard Spring Boot Metrics:**
- JVM memory and garbage collection
- HTTP request metrics (counts, durations)
- Database connection pool metrics
- Kafka consumer lag and throughput

Access metrics at: `http://localhost:8084/actuator/prometheus`

## Grafana Dashboards

The analytics service is designed to be visualized using Grafana dashboards. Recommended dashboards include:

**Event Performance Dashboard:**
- Total events created over time
- RSVP acceptance rate
- Average guests per event
- Event status distribution

**User Engagement Dashboard:**
- Active users over time
- Most engaged users
- Activity type distribution
- User conversion funnel (invited → accepted → checked-in)

**System Health Dashboard:**
- Service uptime
- Kafka consumer lag
- Database query performance
- API response times

Dashboard configurations are managed in the infrastructure repository.

## Resilience4j

The service implements:

- **Circuit Breakers** - Prevention of cascading failures for:
  - `analyticsDatabase` - Protects database operations
  - `defaultCircuitBreaker` - General protection for other operations
- **Retry** - Automatic retry of failed database operations with exponential backoff
- **Rate Limiting** - Request rate limiting to prevent overload
- **Bulkheads** - Resource isolation for concurrent operations

Configuration is managed via `application.yaml` with health indicators exposed through Actuator.

**Example Configuration:**
```yaml
resilience4j:
  circuitbreaker:
    instances:
      analyticsDatabase:
        slidingWindowSize: 20
        failureRateThreshold: 60
        waitDurationInOpenState: 20s
```

## Testing

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=AnalyticsServiceTest

# Run with coverage report
mvn test jacoco:report
```

Tests are located in `src/test/java/com/planify/analytics/` and include:

- `AnalyticsServiceTest` - Analytics data collection and aggregation logic
- Integration tests for Kafka event processing
- GraphQL query tests