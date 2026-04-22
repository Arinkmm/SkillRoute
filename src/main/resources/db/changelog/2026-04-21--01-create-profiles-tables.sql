--liquibase formatted sql
--changeset arinkmm:1
CREATE TABLE account (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'STUDENT',
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    verification_token VARCHAR(255),
    is_verified BOOLEAN DEFAULT FALSE
);

CREATE TABLE specialization (
    id BIGSERIAL PRIMARY KEY,
    direction VARCHAR(50) NOT NULL DEFAULT 'BACKEND',
    language VARCHAR(100) NOT NULL UNIQUE,
    CONSTRAINT check_direction CHECK (direction IN ('BACKEND', 'FRONTEND', 'FULLSTACK'))
);

CREATE TABLE student_profile (
    account_id BIGINT PRIMARY KEY,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    github_url VARCHAR(255),
    specialization_id BIGINT,
    bio TEXT,
    CONSTRAINT fk_student_account FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE,
    CONSTRAINT fk_student_spec FOREIGN KEY (specialization_id) REFERENCES specialization(id) ON DELETE CASCADE
);

CREATE TABLE company_profile (
    account_id BIGINT PRIMARY KEY,
    company_name VARCHAR(150),
    description TEXT,
    website_url VARCHAR(255),
    is_confirmed BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_company_account FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE
);

CREATE INDEX idx_student_profile_spec ON student_profile(specialization_id);