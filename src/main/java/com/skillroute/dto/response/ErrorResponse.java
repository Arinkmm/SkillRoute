package com.skillroute.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Стандартный объект ошибки API")
public class ErrorResponse {
    @Schema(description = "Описание ошибки для пользователя", example = "Пользователь с таким email уже существует")
    private String message;

    @Schema(description = "HTTP статус-код или внутренний код ошибки", example = "400")
    private int errorCode;

    @Schema(description = "Ошибки конкретных полей формы")
    private Map<String, String> fields;
}
