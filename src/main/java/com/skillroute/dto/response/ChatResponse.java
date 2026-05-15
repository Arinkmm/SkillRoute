package com.skillroute.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Данные чата с сообщениями")
public class ChatResponse {
    @Schema(description = "Уникальный идентификатор чата", example = "12")
    private Long chatId;

    @Schema(description = "Имя собеседника текущего пользователя", example = "ООО Ромашка")
    private String opponentName;

    @Schema(description = "Сообщения чата в порядке отправки")
    private List<MessageResponse> messages;
}
