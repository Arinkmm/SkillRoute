package com.skillroute.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Сообщение в чате")
public class MessageResponse {
    @Schema(description = "Уникальный идентификатор сообщения", example = "101")
    private Long id;

    @Schema(description = "ID аккаунта отправителя", example = "5")
    private Long senderId;

    @Schema(description = "Отображаемое имя отправителя", example = "Анна Иванова")
    private String senderName;

    @Schema(description = "Текст сообщения", example = "Здравствуйте! Готова обсудить вакансию.")
    private String text;

    @Schema(description = "Дата и время отправки сообщения", example = "2026-05-15T12:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Признак того, что сообщение отправлено текущим пользователем", example = "true")
    private boolean isMine;
}
