CREATE TABLE activity_log (
    id            BIGSERIAL PRIMARY KEY,
    staff_user_id BIGINT NOT NULL REFERENCES staff_user (id),
    action        VARCHAR(50) NOT NULL,
    entity_type   VARCHAR(50) NOT NULL,
    entity_id     BIGINT,
    details       VARCHAR(500),
    timestamp     TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_activity_log_staff_user_id ON activity_log (staff_user_id);
CREATE INDEX idx_activity_log_timestamp ON activity_log (timestamp DESC);
