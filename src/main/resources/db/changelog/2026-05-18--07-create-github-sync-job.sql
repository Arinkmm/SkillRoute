--liquibase formatted sql
--changeset arinkmm:7
CREATE TABLE github_sync_job (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    message VARCHAR(500),
    confirmed_count INTEGER NOT NULL DEFAULT 0,
    retry_after_at TIMESTAMP,
    started_at TIMESTAMP,
    finished_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_github_sync_job_student FOREIGN KEY (student_id) REFERENCES student_profile(account_id) ON DELETE CASCADE,
    CONSTRAINT check_github_sync_job_status CHECK (status IN ('PENDING', 'RUNNING', 'SUCCESS', 'FAILED'))
);

CREATE UNIQUE INDEX ux_github_sync_job_active_student ON github_sync_job (student_id) WHERE status IN ('PENDING', 'RUNNING');

CREATE INDEX idx_github_sync_job_queue ON github_sync_job (status, retry_after_at, created_at);
