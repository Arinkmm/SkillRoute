--liquibase formatted sql
--changeset arinkmm:4
CREATE TABLE resource (
    id BIGSERIAL PRIMARY KEY,
    skill_id BIGINT,
    resource VARCHAR(255) NOT NULL,
    CONSTRAINT fk_resource_skill FOREIGN KEY (skill_id) REFERENCES skill(id) ON DELETE CASCADE
);