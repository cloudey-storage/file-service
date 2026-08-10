CREATE TABLE outbox_events (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_type VARCHAR(50) NOT NULL,
    aggregate_id   UUID NOT NULL,
    event_type     VARCHAR(50) NOT NULL,
    payload        TEXT NOT NULL,
    processed      BOOLEAN NOT NULL DEFAULT false,
    created_at     TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_outbox_unprocessed ON outbox_events(processed) WHERE processed = false;