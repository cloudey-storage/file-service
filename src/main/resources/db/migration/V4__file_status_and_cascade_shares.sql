-- V4: file lifecycle status (UPLOADING -> ACTIVE -> DELETED, UPLOADING -> FAILED)
--     replaces is_deleted; file_shares are removed together with their file

-- 1. status: add, backfill from is_deleted, enforce
ALTER TABLE files ADD COLUMN status VARCHAR(20);

UPDATE files
SET status = CASE WHEN is_deleted THEN 'DELETED' ELSE 'ACTIVE' END;

ALTER TABLE files ALTER COLUMN status SET NOT NULL;
ALTER TABLE files ADD CONSTRAINT chk_files_status
    CHECK (status IN ('UPLOADING', 'ACTIVE', 'FAILED', 'DELETED'));

-- 2. deleted_at: start of trash retention; backfilled with the closest known time
ALTER TABLE files ADD COLUMN deleted_at TIMESTAMP NULL;

UPDATE files SET deleted_at = updated_at WHERE status = 'DELETED';

ALTER TABLE files ADD CONSTRAINT chk_files_deleted_at
    CHECK ((status = 'DELETED') = (deleted_at IS NOT NULL));

-- 3. replace is_deleted and its partial index
DROP INDEX IF EXISTS idx_files_owner;
ALTER TABLE files DROP COLUMN is_deleted;

CREATE INDEX idx_files_owner_active ON files(owner_id) WHERE status = 'ACTIVE';

-- 4. shares have no meaning without their file: cascade on delete
ALTER TABLE file_shares DROP CONSTRAINT file_shares_file_id_fkey;
ALTER TABLE file_shares ADD CONSTRAINT fk_file_shares_file
    FOREIGN KEY (file_id) REFERENCES files(id) ON DELETE CASCADE;

CREATE INDEX idx_shares_file ON file_shares(file_id);