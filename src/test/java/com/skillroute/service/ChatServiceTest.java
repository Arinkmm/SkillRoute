package com.skillroute.service;

import com.skillroute.TestMessageProperties;
import com.skillroute.exception.FieldValidationException;
import com.skillroute.mapper.ChatMapper;
import com.skillroute.model.*;
import com.skillroute.openapi.model.MessageRequestApi;
import com.skillroute.openapi.model.MessageResponseApi;
import com.skillroute.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private StudentProfileRepository studentRepository;
    @Mock
    private CompanyProfileRepository companyRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private StudentVacancyRepository studentVacancyRepository;

    private ChatService chatService;

    @BeforeEach
    void setUp() {
        chatService = new ChatService(
                chatRepository,
                messageRepository,
                studentRepository,
                companyRepository,
                accountRepository,
                vacancyRepository,
                studentVacancyRepository,
                TestMessageProperties.create(),
                new ChatMapper());
    }

    @Test
    void getOrCreateChatCreatesVacancyScopedChat() {
        StudentProfile student = student(1L);
        CompanyProfile company = company(2L);
        Vacancy vacancy = vacancy(100L, company);

        when(chatRepository.findByStudentAccountIdAndCompanyAccountIdAndVacancyId(1L, 2L, 100L))
                .thenReturn(Optional.empty());
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(companyRepository.findById(2L)).thenReturn(Optional.of(company));
        when(vacancyRepository.findById(100L)).thenReturn(Optional.of(vacancy));
        when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> {
            Chat chat = invocation.getArgument(0);
            chat.setId(10L);
            return chat;
        });

        Long chatId = chatService.getOrCreateChat(1L, 2L, 100L);

        ArgumentCaptor<Chat> chatCaptor = ArgumentCaptor.forClass(Chat.class);
        verify(chatRepository).save(chatCaptor.capture());

        assertThat(chatId).isEqualTo(10L);
        assertThat(chatCaptor.getValue().getStudent()).isSameAs(student);
        assertThat(chatCaptor.getValue().getCompany()).isSameAs(company);
        assertThat(chatCaptor.getValue().getVacancy()).isSameAs(vacancy);
    }

    @Test
    void getOrCreateChatReturnsExistingChatAndRejectsForeignVacancy() {
        CompanyProfile company = company(2L);
        Vacancy foreignVacancy = vacancy(100L, company(3L));

        when(chatRepository.findByStudentAccountIdAndCompanyAccountIdAndVacancyId(1L, 2L, 10L))
                .thenReturn(Optional.of(Chat.builder().id(55L).build()));
        when(chatRepository.findByStudentAccountIdAndCompanyAccountIdAndVacancyId(1L, 2L, 100L))
                .thenReturn(Optional.empty());
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student(1L)));
        when(companyRepository.findById(2L)).thenReturn(Optional.of(company));
        when(vacancyRepository.findById(100L)).thenReturn(Optional.of(foreignVacancy));

        assertThat(chatService.getOrCreateChat(1L, 2L, 10L)).isEqualTo(55L);
        assertThatThrownBy(() -> chatService.getOrCreateChat(1L, 2L, 100L))
                .hasMessage("У вас нет прав на редактирование этой вакансии");
        verify(chatRepository, never()).save(org.mockito.ArgumentMatchers.argThat(chat -> chat.getVacancy() == foreignVacancy));
    }

    @Test
    void sendMessageRejectsStudentBeforeCompanyStartsDialog() {
        Chat chat = chat(10L, student(1L), company(2L), vacancy(100L, company(2L)));
        StudentVacancy application = application(chat, StudentVacancyStatus.INTERVIEW);

        when(chatRepository.findById(10L)).thenReturn(Optional.of(chat));
        when(studentVacancyRepository.findByStudentIdAndVacancyId(1L, 100L)).thenReturn(Optional.of(application));
        when(messageRepository.existsByChatIdAndSenderId(10L, 2L)).thenReturn(false);

        MessageRequestApi request = new MessageRequestApi();
        request.setText("Hello");

        assertThatThrownBy(() -> chatService.sendMessage(10L, 1L, request))
                .isInstanceOf(FieldValidationException.class)
                .hasMessage("Компания должна начать диалог первой");
    }

    @Test
    void sendMessageAllowsCompanyWhenApplicationIsInInterview() {
        StudentProfile student = student(1L);
        CompanyProfile company = company(2L);
        Vacancy vacancy = vacancy(100L, company);
        Chat chat = chat(10L, student, company, vacancy);
        StudentVacancy application = application(chat, StudentVacancyStatus.INTERVIEW);
        Account sender = Account.builder()
                .id(2L)
                .email("company@example.com")
                .password("password")
                .role(Role.COMPANY)
                .companyProfile(company)
                .build();

        when(chatRepository.findById(10L)).thenReturn(Optional.of(chat));
        when(studentVacancyRepository.findByStudentIdAndVacancyId(1L, 100L)).thenReturn(Optional.of(application));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(sender));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message message = invocation.getArgument(0);
            message.setId(20L);
            return message;
        });

        MessageRequestApi request = new MessageRequestApi();
        request.setText("Interview tomorrow?");

        MessageResponseApi response = chatService.sendMessage(10L, 2L, request);

        assertThat(response.getId()).isEqualTo(20L);
        assertThat(response.getText()).isEqualTo("Interview tomorrow?");
        assertThat(response.getSenderId()).isEqualTo(2L);
    }

    @Test
    void sendMessageAllowsStudentAfterCompanyHasStartedDialog() {
        StudentProfile student = student(1L);
        CompanyProfile company = company(2L);
        Vacancy vacancy = vacancy(100L, company);
        Chat chat = chat(10L, student, company, vacancy);
        StudentVacancy application = application(chat, StudentVacancyStatus.REVIEWING);
        Account sender = Account.builder()
                .id(1L)
                .email("student@example.com")
                .role(Role.STUDENT)
                .studentProfile(student)
                .build();

        when(chatRepository.findById(10L)).thenReturn(Optional.of(chat));
        when(studentVacancyRepository.findByStudentIdAndVacancyId(1L, 100L)).thenReturn(Optional.of(application));
        when(messageRepository.existsByChatIdAndSenderId(10L, 2L)).thenReturn(true);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message message = invocation.getArgument(0);
            message.setId(21L);
            return message;
        });

        MessageRequestApi request = new MessageRequestApi();
        request.setText("Yes");

        MessageResponseApi response = chatService.sendMessage(10L, 1L, request);

        assertThat(response.getId()).isEqualTo(21L);
        assertThat(response.getSenderName()).isEqualTo("Student");
        assertThat(response.getMine()).isTrue();
    }

    @Test
    void getChatResponseMarksMessagesAsReadAndReportsClosedApplication() {
        StudentProfile student = student(1L);
        CompanyProfile company = company(2L);
        Vacancy vacancy = vacancy(100L, company);
        Chat chat = chat(10L, student, company, vacancy);
        StudentVacancy rejected = application(chat, StudentVacancyStatus.REJECTED);
        Account sender = Account.builder()
                .id(2L)
                .email("company@example.com")
                .companyProfile(company)
                .build();
        Message message = Message.builder()
                .id(30L)
                .chat(chat)
                .sender(sender)
                .text("Closed")
                .createdAt(LocalDateTime.now())
                .build();

        when(chatRepository.findById(10L)).thenReturn(Optional.of(chat));
        when(messageRepository.findAllByChatIdOrderByCreatedAtAsc(10L)).thenReturn(List.of(message));
        when(studentVacancyRepository.findByStudentIdAndVacancyId(1L, 100L)).thenReturn(Optional.of(rejected));

        var response = chatService.getChatResponse(10L, 1L);

        verify(messageRepository).markAsReadInChat(10L, 1L);
        assertThat(response.getClosed()).isTrue();
        assertThat(response.getVacancyName()).isEqualTo("Java Developer");
        assertThat(response.getMessages()).hasSize(1);
        assertThat(response.getMessages().getFirst().getMine()).isFalse();
    }

    @Test
    void getPreviewsHidesStudentChatsBeforeCompanyMessageAndSortsVisibleChats() {
        StudentProfile student = student(1L);
        CompanyProfile company = company(2L);
        Chat hidden = chat(10L, student, company, vacancy(100L, company));
        hidden.setCreatedAt(LocalDateTime.now().minusDays(1));
        Chat visible = chat(11L, student, company, vacancy(101L, company));
        visible.setCreatedAt(LocalDateTime.now());
        Message last = Message.builder()
                .id(40L)
                .chat(visible)
                .sender(Account.builder().id(2L).companyProfile(company).build())
                .text("Interview")
                .createdAt(LocalDateTime.now())
                .build();

        when(chatRepository.findAllById(1L)).thenReturn(List.of(hidden, visible));
        when(messageRepository.existsByChatIdAndSenderId(10L, 2L)).thenReturn(false);
        when(messageRepository.existsByChatIdAndSenderId(11L, 2L)).thenReturn(true);
        when(messageRepository.findFirstByChatIdOrderByCreatedAtDesc(11L)).thenReturn(Optional.of(last));

        var previews = chatService.getPreviews(1L);

        assertThat(previews).hasSize(1);
        assertThat(previews.getFirst().getChatId()).isEqualTo(11L);
        assertThat(previews.getFirst().getVacancyName()).isEqualTo("Java Developer");
    }

    private StudentProfile student(Long id) {
        return StudentProfile.builder().id(id).firstName("Student").build();
    }

    private CompanyProfile company(Long id) {
        return CompanyProfile.builder().id(id).companyName("Company").build();
    }

    private Vacancy vacancy(Long id, CompanyProfile company) {
        return Vacancy.builder().id(id).name("Java Developer").company(company).build();
    }

    private Chat chat(Long id, StudentProfile student, CompanyProfile company, Vacancy vacancy) {
        return Chat.builder()
                .id(id)
                .student(student)
                .company(company)
                .vacancy(vacancy)
                .build();
    }

    private StudentVacancy application(Chat chat, StudentVacancyStatus status) {
        return StudentVacancy.builder()
                .student(chat.getStudent())
                .vacancy(chat.getVacancy())
                .status(status)
                .build();
    }
}
