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
    private final StudentVacancyRepository studentVacancyRepository;
    private final MessageProperties messages;
    private final ChatMapper chatMapper;

    @Transactional
    public Long getOrCreateChat(Long studentId, Long companyId) {
        return chatRepository.findByStudentAccountIdAndCompanyAccountId(studentId, companyId)
                .map(Chat::getId)
                .orElseGet(() -> {
                    StudentProfile student = studentRepository.findById(studentId)
                            .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getStudentNotFound()));
                    CompanyProfile company = companyRepository.findById(companyId)
                            .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getCompanyNotFound()));
                    Chat chat = Chat.builder().student(student).company(company).build();
                    return chatRepository.save(chat).getId();
                });
    }

    @Transactional
    public MessageResponseApi sendMessage(Long chatId, Long senderId, MessageRequestApi request) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getChatNotFound()));
        validateChatParticipant(chat, senderId);
        if (isClosed(chat)) {
            throw new FieldValidationException(messages.getChat().getClosed(), Map.of("text", messages.getChat().getClosed()));
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

        StudentVacancy activeApplication = findActiveApplication(chat);
        boolean closed = activeApplication != null && isTerminal(activeApplication.getStatus());

        return chatMapper.toChatResponse(chat, currentUserId, activeApplication, messages, closed);
    }

    private StudentVacancy findActiveApplication(Chat chat) {
        return studentVacancyRepository.findAllByStudentIdAndCompanyId(chat.getStudent().getId(), chat.getCompany().getId()).stream()
                .max((left, right) -> Integer.compare(statusPriority(left.getStatus()), statusPriority(right.getStatus())))
                .orElse(null);
    }

    private int statusPriority(StudentVacancyStatus status) {
        return switch (status) {
            case INTERVIEW -> 5;
            case REVIEWING -> 4;
            case SUBMITTED -> 3;
            case ACCEPTED -> 2;
            case REJECTED -> 1;
        };
    }

    @Transactional(readOnly = true)
    public List<ChatPreviewResponse> getPreviews(Long userId) {
        return chatRepository.findAllById(userId).stream()
                .map(chat -> {
                    Message last = messageRepository.findFirstByChatIdOrderByCreatedAtDesc(chat.getId()).orElse(null);
                    return chatMapper.toPreviewResponse(chat, last, userId, messages.getEntity().getNoMessages());
                })
                .sorted((a, b) -> b.getLastMessageTime().compareTo(a.getLastMessageTime()))
                .toList();
    }

    private boolean isClosed(Chat chat) {
        StudentVacancy activeApplication = findActiveApplication(chat);
        return activeApplication != null && isTerminal(activeApplication.getStatus());
    }

    private boolean isTerminal(StudentVacancyStatus status) {
        return status == StudentVacancyStatus.ACCEPTED || status == StudentVacancyStatus.REJECTED;
    }

    private void validateChatParticipant(Chat chat, Long currentUserId) {
        boolean isStudent = chat.getStudent() != null && chat.getStudent().getId().equals(currentUserId);
        boolean isCompany = chat.getCompany() != null && chat.getCompany().getId().equals(currentUserId);

        if (!isStudent && !isCompany) {
            throw new ResourceOwnershipException(messages.getChat().getAccessForbidden());
        }
    }
}
