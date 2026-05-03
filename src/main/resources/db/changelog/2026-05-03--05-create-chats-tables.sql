--liquibase formatted sql
--changeset arinkmm:5
CREATE TABLE chat (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_chat_student FOREIGN KEY (student_id) REFERENCES student_profile(account_id) ON DELETE CASCADE,
    CONSTRAINT fk_chat_company FOREIGN KEY (company_id) REFERENCES company_profile(account_id) ON DELETE CASCADE,
    CONSTRAINT uq_chat_participants UNIQUE (student_id, company_id)
);

CREATE INDEX idx_chat_student ON chat(student_id);
CREATE INDEX idx_chat_company ON chat(company_id);

CREATE TABLE message (
    id BIGSERIAL PRIMARY KEY,
    chat_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    text TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_message_chat FOREIGN KEY (chat_id) REFERENCES chat(id) ON DELETE CASCADE,
    CONSTRAINT fk_message_sender FOREIGN KEY (sender_id) REFERENCES account(id) ON DELETE CASCADE
);

CREATE INDEX idx_message_chat ON message(chat_id);