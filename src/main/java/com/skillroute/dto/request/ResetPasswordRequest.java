package com.skillroute.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на сброс пароля через токен из почты")
public class ResetPasswordRequest {

    @Schema(
            description = "Уникальный токен восстановления, полученный из ссылки в письме",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    @NotBlank(message = "Ссылка восстановления недействительна")
    private String token;

    @Schema(
            description = "Новый пароль пользователя (мин. 8 символов, цифра, заглавная и строчная буквы)",
            example = "Password123!",
            type = "string",
            format = "password"
    )
    @NotBlank(message = "Новый пароль обязателен")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$",
            message = "Пароль должен содержать минимум 8 символов, хотя бы одну цифру, строчную и заглавную букву (латиница)"
    )
    private String newPassword;

    @Schema(
            description = "Подтверждение нового пароля (должно совпадать с newPassword)",
            example = "Password123!",
            type = "string",
            format = "password"
    )
    @NotBlank(message = "Подтверждение пароля обязательно")
    private String confirmNewPassword;
}