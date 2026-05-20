package com.skillroute.mapper;

import com.skillroute.dto.response.ChatPreviewResponse;
import com.skillroute.model.Account;
import com.skillroute.model.Chat;
import com.skillroute.model.Message;
import com.skillroute.model.StudentVacancy;
import com.skillroute.openapi.model.ChatResponseApi;
import com.skillroute.openapi.model.MessageResponseApi;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChatMapper {

    public MessageResponseApi toMessageResponse(Message message, Long currentUserId) {
        MessageResponseApi response = new MessageResponseApi();
        response.setId(message.getId());
        response.setSenderId(message.getSender().getId());
        response.setSenderName(resolveSenderName(message.getSender()));
        response.setText(message.getText());
        response.setCreatedAt(message.getCreatedAt() == null ? null : message.getCreatedAt().toString());
        response.setMine(message.getSender().getId().equals(currentUserId));
        return response;
    }

    public ChatResponseApi toChatResponse(Chat chat,
                                          Long currentUserId,
                                          StudentVacancy activeApplication,
                                          List<MessageResponseApi> messages,
                                          boolean closed) {
        ChatResponseApi response = new ChatResponseApi();
        response.setChatId(chat.getId());
        response.setOpponentName(resolveOpponentName(chat, currentUserId));
        response.setMessages(messages);
        response.setCompanyView(chat.getCompany().getId().equals(currentUserId));
        response.setStudentId(chat.getStudent().getId());
        response.setVacancyId(activeApplication != null ? activeApplication.getVacancy().getId() : null);
        response.setVacancyName(activeApplication != null ? activeApplication.getVacancy().getName() : null);
        response.setStudentVacancyStatus(activeApplication != null ? activeApplication.getStatus().name() : null);
        response.setClosed(closed);
        return response;
    }

    public ChatPreviewResponse toPreviewResponse(Chat chat, Message lastMessage, Long currentUserId, String emptyMessage) {
        return ChatPreviewResponse.builder()
                .chatId(chat.getId())
                .opponentName(resolveOpponentName(chat, currentUserId))
                .vacancyName(chat.getVacancy() != null ? chat.getVacancy().getName() : null)
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
