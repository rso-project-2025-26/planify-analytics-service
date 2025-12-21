-- Event Metrics Table
CREATE TABLE event_metrics (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    organization_id UUID NOT NULL,
    event_title VARCHAR(255) NOT NULL,
    event_date TIMESTAMP NOT NULL,
    event_status VARCHAR(50),
    
    -- Metrics
    total_invites INTEGER DEFAULT 0,
    rsvp_accepted INTEGER DEFAULT 0,
    rsvp_declined INTEGER DEFAULT 0,
    rsvp_maybe INTEGER DEFAULT 0,
    checked_in INTEGER DEFAULT 0,
    
    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(event_id)
);

-- User Activity Table
CREATE TABLE user_activity (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    event_id BIGINT NOT NULL,
    activity_type VARCHAR(50) NOT NULL,
    activity_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- System Metrics Table
CREATE TABLE system_metrics (
    id BIGSERIAL PRIMARY KEY,
    metric_name VARCHAR(100) NOT NULL,
    metric_value DOUBLE PRECISION NOT NULL,
    metric_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for better query performance
CREATE INDEX idx_event_metrics_event_id ON event_metrics(event_id);
CREATE INDEX idx_event_metrics_org_id ON event_metrics(organization_id);
CREATE INDEX idx_user_activity_user_id ON user_activity(user_id);
CREATE INDEX idx_user_activity_event_id ON user_activity(event_id);
CREATE INDEX idx_system_metrics_name ON system_metrics(metric_name);
CREATE INDEX idx_system_metrics_timestamp ON system_metrics(metric_timestamp);