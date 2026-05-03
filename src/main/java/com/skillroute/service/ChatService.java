package com.skillroute.service;

import com.skillroute.dto.request.MessageRequest;
import com.skillroute.dto.response.*;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.model.*;
import com.skillroute.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final StudentProfileRepository studentRepository;
    private final CompanyProfileRepository companyRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public Long getOrCreateChat(Long studentId, Long companyId) {
        return chatRepository.findByStudentAccountIdAndCompanyAccountId(studentId, companyId)
                .map(Chat::getId)
                .orElseGet(() -> {
                    var student = studentRepository.findById(studentId).orElseThrow();
                    var company = companyRepository.findById(companyId).orElseThrow();
                    Chat chat = Chat.builder().student(student).company(company).build();
                    return chatRepository.save(chat).getId();
                });
    }

    @Transactional
    public MessageResponse sendMessage(Long chatId, Long senderId, MessageRequest request) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Чат не найден"));

        Account sender = accountRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Аккаунт не найден"));

        Message message = Message.builder()
                .chat(chat)
                .sender(sender)
                .text(request.getText())
                .build();

        Message savedMessage = messageRepository.save(message);

        return MessageResponse.builder()
                .id(savedMessage.getId())
                .senderId(senderId)
                .text(savedMessage.getText())
                .createdAt(savedMessage.getCreatedAt())
                .isMine(true)
                .build();
    }

    @Transactional
    public ChatResponse getChatResponse(Long chatId, Long currentUserId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow();
        messageRepository.markAsReadInChat(chatId, currentUserId);

        String opponentName = chat.getStudent().getId().equals(currentUserId)
                ? chat.getCompany().getCompanyName() : chat.getStudent().getFirstName();

        List<MessageResponse> messages = messageRepository.findAllByChatIdOrderByCreatedAtAsc(chatId).stream()
                .map(m -> MessageResponse.builder()
                        .id(m.getId()).senderId(m.getSender().getId()).text(m.getText())
                        .createdAt(m.getCreatedAt()).isMine(m.getSender().getId().equals(currentUserId))
                        .build()).toList();

        return ChatResponse.builder().chatId(chatId).opponentName(opponentName).messages(messages).build();
    }

    @Transactional(readOnly = true)
    public List<ChatPreviewResponse> getPreviews(Long userId) {
        return chatRepository.findAllByAccountId(userId).stream()
                .map(chat -> {
                    Message last = messageRepository.findFirstByChatIdOrderByCreatedAtDesc(chat.getId()).orElse(null);
                    String name = chat.getStudent().getId().equals(userId)
                            ? chat.getCompany().getCompanyName() : chat.getStudent().getFirstName();
                    return ChatPreviewResponse.builder()
                            .chatId(chat.getId()).opponentName(name)
                            .lastMessage(last != null ? last.getText() : "Нет сообщений")
                            .lastMessageTime(last != null ? last.getCreatedAt() : chat.getCreatedAt())
                            .build();
                })
                .sorted((a, b) -> b.getLastMessageTime().compareTo(a.getLastMessageTime()))
                .toList();
    }
}