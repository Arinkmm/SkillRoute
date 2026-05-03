package com.skillroute.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatPreviewResponse {
    private Long chatId;
    private String opponentName;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
}