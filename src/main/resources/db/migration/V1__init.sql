-- Event Metrics Table
CREATE TABLE event_metrics (
    id BIGSERIAL PRIMARY KEY,
    event_id UUID NOT NULL UNIQUE,
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
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_event_metrics_event_id ON event_metrics(event_id);
CREATE INDEX idx_event_metrics_organization_id ON event_metrics(organization_id);
CREATE INDEX idx_event_metrics_status ON event_metrics(event_status);
CREATE INDEX idx_event_metrics_date ON event_metrics(event_date);

-- User Activity Table
CREATE TABLE user_activity (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    event_id UUID NOT NULL,
    activity_type VARCHAR(50) NOT NULL,
    activity_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
	
CREATE INDEX idx_user_activity_user_id ON user_activity(user_id);
CREATE INDEX idx_user_activity_event_id ON user_activity(event_id);
CREATE INDEX idx_user_activity_type ON user_activity(activity_type);
CREATE INDEX idx_user_activity_timestamp ON user_activity(activity_timestamp);

-- System Metrics Table
CREATE TABLE system_metrics (
    id BIGSERIAL PRIMARY KEY,
    metric_name VARCHAR(100) NOT NULL,
    metric_value DOUBLE PRECISION NOT NULL,
    metric_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_system_metrics_name ON system_metrics(metric_name);
CREATE INDEX idx_system_metrics_timestamp ON system_metrics(metric_timestamp);