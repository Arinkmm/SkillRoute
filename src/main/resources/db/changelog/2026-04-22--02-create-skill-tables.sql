--liquibase formatted sql
--changeset arinkmm:2
CREATE TABLE skill (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE student_skill (
    student_id BIGINT,
    skill_id BIGINT,
    is_confirmed_by_github BOOLEAN DEFAULT FALSE,
    level INT NOT NULL DEFAULT 1 CONSTRAINT check_student_skill_level CHECK (level >= 1 AND level <= 5),
    PRIMARY KEY (student_id, skill_id),
    CONSTRAINT fk_student_skill_student FOREIGN KEY (student_id) REFERENCES account(id) ON DELETE CASCADE,
    CONSTRAINT fk_student_skill_skill FOREIGN KEY (skill_id) REFERENCES skill(id) ON DELETE CASCADE
);

CREATE INDEX idx_student_skill_skill_id ON student_skill(skill_id);