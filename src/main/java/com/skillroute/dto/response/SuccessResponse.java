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
@Schema(description = "Универсальный объект успешного ответа")
public class SuccessResponse {
    @Schema(description = "Текстовое сообщение о результате операции", example = "Синхронизация успешно завершена")
    private String message;
}
