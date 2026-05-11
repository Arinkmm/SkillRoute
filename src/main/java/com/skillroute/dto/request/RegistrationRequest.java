package com.skillroute.dto.request;

import com.skillroute.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на регистрацию нового пользователя")
public class RegistrationRequest {

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    @Schema(description = "Электронная почта пользователя", example = "developer@skillroute.com")
    private String email;

    @NotBlank(message = "Пароль обязателен")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$",
            message = "Пароль должен содержать минимум 8 символов, хотя бы одну цифру, строчную и заглавную букву (латиница)"
    )
    @Schema(
            description = "Пароль пользователя (мин. 8 символов, цифра, заглавная и строчная буквы)",
            example = "Password123!",
            type = "string",
            format = "password"
    )
    private String password;

    @NotBlank(message = "Подтверждение пароля обязательно")
    @Schema(description = "Повторный ввод пароля для проверки", example = "Password123!")
    private String confirmPassword;

    @Schema(description = "Роль пользователя в системе", example = "STUDENT")
    @NotNull(message = "Выберите роль")
    private Role role;
}
