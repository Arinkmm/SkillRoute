package com.skillroute.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на отправку текстового сообщения")
public class MessageRequest {

    @NotBlank(message = "Текст сообщения не может быть пустым")
    @Size(max = 500, message = "Сообщение не должно превышать 500 символов")
    @Schema(description = "Содержание сообщения", example = "Привет! Подскажи, какие технологии ты использовал в проекте?")
    private String text;
}