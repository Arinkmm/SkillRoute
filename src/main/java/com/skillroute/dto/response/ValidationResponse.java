package com.skillroute.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Результат валидации поля")
public class ValidationResponse {
    @Schema(description = "Статус валидности", example = "true")
    private boolean valid;

    @Schema(description = "Сообщение для пользователя", example = "Данные корректны")
    private String message;
}