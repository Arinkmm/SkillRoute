--liquibase formatted sql
--changeset arinkmm:6
CREATE TABLE skill_dictionary (
    id BIGSERIAL PRIMARY KEY,
    skill_id BIGINT NOT NULL UNIQUE,
    import_pattern VARCHAR(255) NOT NULL,
    quick_signals VARCHAR(500),
    CONSTRAINT fk_skill_dictionary_skill FOREIGN KEY (skill_id) REFERENCES skill(id) ON DELETE CASCADE
);

CREATE INDEX idx_skill_dict_pattern ON skill_dictionary (import_pattern);
CREATE INDEX idx_skill_dict_signals ON skill_dictionary (quick_signals);