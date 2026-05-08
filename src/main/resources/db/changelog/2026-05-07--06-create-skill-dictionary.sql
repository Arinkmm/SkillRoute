--liquibase formatted sql
--changeset arinkmm:6
CREATE TABLE skill_dictionary (
    id BIGSERIAL PRIMARY KEY,
    skill_id BIGINT NOT NULL UNIQUE,
    import_pattern VARCHAR(255) NOT NULL,
    CONSTRAINT fk_skill_dictionary_skill FOREIGN KEY (skill_id) REFERENCES skill(id) ON DELETE CASCADE
);

INSERT INTO skill (name) VALUES ('Kafka'), ('PostgreSQL'), ('Redis') ON CONFLICT DO NOTHING;

INSERT INTO skill_dictionary (skill_id, import_pattern)
VALUES
    ((SELECT id FROM skill WHERE name = 'Kafka'), 'org.springframework.kafka'),
    ((SELECT id FROM skill WHERE name = 'PostgreSQL'), 'java.sql'),
    ((SELECT id FROM skill WHERE name = 'Redis'), 'org.springframework.data.redis');