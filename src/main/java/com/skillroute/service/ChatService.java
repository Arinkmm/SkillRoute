package com.skillroute.service;

import com.skillroute.dto.response.ChatPreviewResponse;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.exception.FieldValidationException;
import com.skillroute.exception.ResourceOwnershipException;
import com.skillroute.mapper.ChatMapper;
import com.skillroute.model.*;
import com.skillroute.openapi.model.ChatResponseApi;
import com.skillroute.openapi.model.MessageRequestApi;
import com.skillroute.openapi.model.MessageResponseApi;
import com.skillroute.properties.MessageProperties;
import com.skillroute.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final StudentProfileRepository studentRepository;
    private final CompanyProfileRepository companyRepository;
    private final AccountRepository accountRepository;
    private final VacancyRepository vacancyRepository;
    private final StudentVacancyRepository studentVacancyRepository;
    private final MessageProperties messages;
    private final ChatMapper chatMapper;

    @Transactional
    public Long getOrCreateChat(Long studentId, Long companyId, Long vacancyId) {
        return chatRepository.findByStudentAccountIdAndCompanyAccountIdAndVacancyId(studentId, companyId, vacancyId)
                .map(Chat::getId)
                .orElseGet(() -> {
                    StudentProfile student = studentRepository.findById(studentId)
                            .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getStudentNotFound()));
                    CompanyProfile company = companyRepository.findById(companyId)
                            .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getCompanyNotFound()));
                    Vacancy vacancy = vacancyRepository.findById(vacancyId)
                            .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getVacancyNotFound()));

                    if (!vacancy.getCompany().getId().equals(companyId)) {
                        throw new ResourceOwnershipException(messages.getVacancy().getEditForbidden());
                    }

                    Chat chat = Chat.builder()
                            .student(student)
                            .company(company)
                            .vacancy(vacancy)
                            .build();
                    return chatRepository.save(chat).getId();
                });
    }

    @Transactional
    public MessageResponseApi sendMessage(Long chatId, Long senderId, MessageRequestApi request) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getChatNotFound()));
        validateChatParticipant(chat, senderId);
        StudentVacancy application = findChatApplication(chat);
        if (!canSendMessages(chat, senderId, application)) {
            String message = isStudent(chat, senderId)
                    ? messages.getChat().getStudentStartForbidden()
                    : messages.getChat().getClosed();
            throw new FieldValidationException(message, Map.of("text", message));
        }

        Account sender = accountRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getAccountNotFound()));

        Message message = Message.builder()
                .chat(chat)
                .sender(sender)
                .text(request.getText())
                .build();

        Message savedMessage = messageRepository.save(message);

        return chatMapper.toMessageResponse(savedMessage, senderId);
    }

    @Transactional
    public ChatResponseApi getChatResponse(Long chatId, Long currentUserId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getChatNotFound()));
        validateChatParticipant(chat, currentUserId);
        messageRepository.markAsReadInChat(chatId, currentUserId);

        List<MessageResponseApi> messages = messageRepository.findAllByChatIdOrderByCreatedAtAsc(chatId).stream()
                .map(message -> chatMapper.toMessageResponse(message, currentUserId))
                .toList();

        StudentVacancy activeApplication = findChatApplication(chat);
        boolean closed = !canSendMessages(chat, currentUserId, activeApplication);

        return chatMapper.toChatResponse(chat, currentUserId, activeApplication, messages, closed);
    }

    private StudentVacancy findChatApplication(Chat chat) {
        if (chat.getVacancy() != null) {
            return studentVacancyRepository.findByStudentIdAndVacancyId(chat.getStudent().getId(), chat.getVacancy().getId())
                    .orElse(null);
        }

        return studentVacancyRepository.findAllByStudentIdAndCompanyId(chat.getStudent().getId(), chat.getCompany().getId()).stream()
                .max((left, right) -> {
                    int priority = Integer.compare(statusPriority(left.getStatus()), statusPriority(right.getStatus()));
                    if (priority != 0) {
                        return priority;
                    }
                    return Long.compare(left.getVacancy().getId(), right.getVacancy().getId());
                })
                .orElse(null);
    }

    private int statusPriority(StudentVacancyStatus status) {
        return switch (status) {
            case INTERVIEW -> 5;
            case REVIEWING -> 4;
            case ACCEPTED, REJECTED -> 3;
            case SUBMITTED -> 1;
        };
    }

    @Transactional(readOnly = true)
    public List<ChatPreviewResponse> getPreviews(Long userId) {
        return chatRepository.findAllById(userId).stream()
                .filter(chat -> isCompany(chat, userId) || hasCompanyMessage(chat))
                .map(chat -> {
                    Message last = messageRepository.findFirstByChatIdOrderByCreatedAtDesc(chat.getId()).orElse(null);
                    return chatMapper.toPreviewResponse(chat, last, userId, messages.getEntity().getNoMessages());
                })
                .sorted((a, b) -> b.getLastMessageTime().compareTo(a.getLastMessageTime()))
                .toList();
    }

    private boolean canSendMessages(Chat chat, Long currentUserId, StudentVacancy application) {
        if (!isOpenForMessaging(application)) {
            return false;
        }

        return isCompany(chat, currentUserId) || hasCompanyMessage(chat);
    }

    private boolean isOpenForMessaging(StudentVacancy application) {
        return application != null
                && (application.getStatus() == StudentVacancyStatus.REVIEWING
                || application.getStatus() == StudentVacancyStatus.INTERVIEW);
    }

    private boolean hasCompanyMessage(Chat chat) {
        return messageRepository.existsByChatIdAndSenderId(chat.getId(), chat.getCompany().getId());
    }

    private void validateChatParticipant(Chat chat, Long currentUserId) {
        boolean isStudent = isStudent(chat, currentUserId);
        boolean isCompany = isCompany(chat, currentUserId);

        if (!isStudent && !isCompany) {
            throw new ResourceOwnershipException(messages.getChat().getAccessForbidden());
        }
    }

    private boolean isStudent(Chat chat, Long currentUserId) {
        return chat.getStudent() != null && chat.getStudent().getId().equals(currentUserId);
    }

    private boolean isCompany(Chat chat, Long currentUserId) {
        return chat.getCompany() != null && chat.getCompany().getId().equals(currentUserId);
    }
}
