CREATE TABLE file_shares (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    file_id              UUID NOT NULL REFERENCES files(id),
    shared_with_user_id  UUID NOT NULL,
    permission           VARCHAR(20) NOT NULL DEFAULT 'READ',
    shared_at            TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_shares_user ON file_shares(shared_with_user_id);