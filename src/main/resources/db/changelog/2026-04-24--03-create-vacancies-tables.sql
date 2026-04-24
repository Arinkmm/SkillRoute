--liquibase formatted sql
--changeset arinkmm:3
CREATE TABLE vacancy (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT,
    name VARCHAR(255) NOT NULL UNIQUE,
    CONSTRAINT fk_vacancy_company FOREIGN KEY (company_id) REFERENCES company_profile(account_id) ON DELETE CASCADE
);

CREATE TABLE vacancy_profile (
    vacancy_id BIGINT PRIMARY KEY ,
    specialization_id BIGINT,
    salary BIGINT,
    work_schedule VARCHAR(100),
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    CONSTRAINT fk_profile_vacancy FOREIGN KEY (vacancy_id) REFERENCES vacancy(id) ON DELETE CASCADE,
    CONSTRAINT fk_vacancy_specialization FOREIGN KEY (specialization_id) REFERENCES specialization(id) ON DELETE CASCADE,
    CONSTRAINT check_work_schedule CHECK (work_schedule IN ('FULL_TIME', 'PART_TIME', 'REMOTE', 'HYBRID', 'FLEXIBLE')),
    CONSTRAINT check_vacancy_status CHECK (status IN ('OPEN', 'CLOSE', 'IN_PROGRESS'))
);

CREATE TABLE vacancy_skill (
    vacancy_id BIGINT,
    skill_id BIGINT,
    level INT NOT NULL DEFAULT 1 CONSTRAINT check_vacancy_skill_level CHECK (level >= 1 AND level <= 5),
    PRIMARY KEY (vacancy_id, skill_id),
    CONSTRAINT fk_vacancy_skill_vacancy FOREIGN KEY (vacancy_id) REFERENCES vacancy(id) ON DELETE CASCADE,
    CONSTRAINT fk_vacancy_skill_skill FOREIGN KEY (skill_id) REFERENCES skill(id) ON DELETE CASCADE
);

CREATE TABLE student_vacancy (
    student_id BIGINT,
    vacancy_id BIGINT,
    status VARCHAR(50) NOT NULL DEFAULT 'SUBMITTED',
    PRIMARY KEY (student_id, vacancy_id),
    CONSTRAINT fk_student_vacancy_student FOREIGN KEY (student_id) REFERENCES account(id) ON DELETE CASCADE,
    CONSTRAINT fk_student_vacancy_vacancy FOREIGN KEY (vacancy_id) REFERENCES vacancy(id) ON DELETE CASCADE,
    CONSTRAINT check_student_vacancy_status CHECK (status IN ('SUBMITTED', 'REVIEWING', 'INTERVIEW', 'REJECTED', 'ACCEPTED'))
);

CREATE INDEX idx_vacancy_company_id ON vacancy(company_id);
CREATE INDEX idx_vacancy_profile_salary ON vacancy_profile(salary);
CREATE INDEX idx_vacancy_profile_status ON vacancy_profile(status);
CREATE INDEX idx_student_vacancy_vacancy_id ON student_vacancy(vacancy_id);