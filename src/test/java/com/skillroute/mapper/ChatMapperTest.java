package com.skillroute.mapper;

import com.skillroute.dto.response.ChatPreviewResponse;
import com.skillroute.model.Account;
import com.skillroute.model.Chat;
import com.skillroute.model.CompanyProfile;
import com.skillroute.model.Message;
import com.skillroute.model.Role;
import com.skillroute.model.StudentProfile;
import com.skillroute.model.StudentVacancy;
import com.skillroute.model.StudentVacancyStatus;
import com.skillroute.model.Vacancy;
import com.skillroute.openapi.model.ChatResponseApi;
import com.skillroute.openapi.model.MessageResponseApi;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ChatMapperTest {
    private final ChatMapper mapper = new ChatMapper();

    @Test
    void toPreviewResponseUsesVacancyNameAndLastMessage() {
        Chat chat = chat();
        Account companyAccount = Account.builder()
                .id(2L)
                .email("company@example.com")
                .role(Role.COMPANY)
                .companyProfile(chat.getCompany())
                .build();
        Message message = Message.builder()
                .id(20L)
                .chat(chat)
                .sender(companyAccount)
                .text("Hello")
                .createdAt(LocalDateTime.of(2026, 5, 20, 10, 15))
                .build();

        ChatPreviewResponse response = mapper.toPreviewResponse(chat, message, 1L, "Сообщений пока нет");

        assertThat(response.getChatId()).isEqualTo(10L);
        assertThat(response.getOpponentName()).isEqualTo("Company");
        assertThat(response.getVacancyName()).isEqualTo("Java Developer");
        assertThat(response.getLastMessage()).isEqualTo("Hello");
        assertThat(response.getLastMessageTime()).isEqualTo(LocalDateTime.of(2026, 5, 20, 10, 15));
    }

    @Test
    void toChatResponseMapsApplicationContextAndClosedFlag() {
        Chat chat = chat();
        StudentVacancy application = StudentVacancy.builder()
                .student(chat.getStudent())
                .vacancy(chat.getVacancy())
                .status(StudentVacancyStatus.INTERVIEW)
                .build();
        MessageResponseApi message = new MessageResponseApi();
        message.setText("Hello");

        ChatResponseApi response = mapper.toChatResponse(chat, 2L, application, List.of(message), false);

        assertThat(response.getChatId()).isEqualTo(10L);
        assertThat(response.getOpponentName()).isEqualTo("Maria");
        assertThat(response.getVacancyId()).isEqualTo(100L);
        assertThat(response.getVacancyName()).isEqualTo("Java Developer");
        assertThat(response.getStudentVacancyStatus()).isEqualTo("INTERVIEW");
        assertThat(response.getClosed()).isFalse();
        assertThat(response.getMessages()).containsExactly(message);
    }

    private Chat chat() {
        StudentProfile student = StudentProfile.builder()
                .id(1L)
                .firstName("Maria")
                .build();
        CompanyProfile company = CompanyProfile.builder()
                .id(2L)
                .companyName("Company")
                .build();
        Vacancy vacancy = Vacancy.builder()
                .id(100L)
                .name("Java Developer")
                .company(company)
                .build();

        return Chat.builder()
                .id(10L)
                .student(student)
                .company(company)
                .vacancy(vacancy)
                .createdAt(LocalDateTime.of(2026, 5, 20, 9, 0))
                .build();
    }
}
