CREATE TABLE files (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id        UUID NOT NULL,
    original_name   VARCHAR(255) NOT NULL,
    minio_key       VARCHAR(500) NOT NULL UNIQUE,
    content_type    VARCHAR(100),
    size_bytes      BIGINT NOT NULL,
    is_deleted      BOOLEAN NOT NULL DEFAULT false,
    uploaded_at     TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_files_owner ON files(owner_id) WHERE is_deleted = false;