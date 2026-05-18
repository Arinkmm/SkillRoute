package com.skillroute.mapper;

import com.skillroute.dto.response.ChatPreviewResponse;
import com.skillroute.dto.response.ChatResponse;
import com.skillroute.dto.response.MessageResponse;
import com.skillroute.model.Account;
import com.skillroute.model.Chat;
import com.skillroute.model.Message;
import com.skillroute.model.StudentVacancy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChatMapper {

    public MessageResponse toMessageResponse(Message message, Long currentUserId) {
        return MessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .senderName(resolveSenderName(message.getSender()))
                .text(message.getText())
                .createdAt(message.getCreatedAt())
                .isMine(message.getSender().getId().equals(currentUserId))
                .build();
    }

    public ChatResponse toChatResponse(Chat chat,
                                       Long currentUserId,
                                       StudentVacancy activeApplication,
                                       List<MessageResponse> messages,
                                       boolean closed) {
        return ChatResponse.builder()
                .chatId(chat.getId())
                .opponentName(resolveOpponentName(chat, currentUserId))
                .messages(messages)
                .companyView(chat.getCompany().getId().equals(currentUserId))
                .studentId(chat.getStudent().getId())
                .vacancyId(activeApplication != null ? activeApplication.getVacancy().getId() : null)
                .vacancyName(activeApplication != null ? activeApplication.getVacancy().getName() : null)
                .studentVacancyStatus(activeApplication != null ? activeApplication.getStatus() : null)
                .closed(closed)
                .build();
    }

    public ChatPreviewResponse toPreviewResponse(Chat chat, Message lastMessage, Long currentUserId, String emptyMessage) {
        return ChatPreviewResponse.builder()
                .chatId(chat.getId())
                .opponentName(resolveOpponentName(chat, currentUserId))
                .lastMessage(lastMessage != null ? lastMessage.getText() : emptyMessage)
                .lastMessageTime(lastMessage != null ? lastMessage.getCreatedAt() : chat.getCreatedAt())
                .build();
    }

    private String resolveOpponentName(Chat chat, Long currentUserId) {
        return chat.getStudent().getId().equals(currentUserId)
                ? chat.getCompany().getCompanyName()
                : chat.getStudent().getFirstName();
    }

    private String resolveSenderName(Account sender) {
        if (sender.getStudentProfile() != null) {
            String firstName = sender.getStudentProfile().getFirstName();
            String lastName = sender.getStudentProfile().getLastName();
            return ((firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName)).trim();
        }
        if (sender.getCompanyProfile() != null) {
            return sender.getCompanyProfile().getCompanyName();
        }
        return sender.getEmail();
    }
}
