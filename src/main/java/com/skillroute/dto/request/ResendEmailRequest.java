package com.skillroute.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "Запрос на повторную отправку письма верификации")
public class ResendEmailRequest {

    @NotBlank(message = "Email обязателен")
    @Email(message = "Неверный формат почты")
    @Schema(description = "Email, на который нужно отправить ссылку", example = "user@mail.com")
    private String email;
}