package com.skillroute.dto.request;

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
public class EditPasswordRequest {
    @NotBlank(message = "Старый пароль обязателен")
    private String oldPassword;

    @NotBlank(message = "Новый пароль обязателен")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$",
            message = "Пароль должен содержать минимум 8 символов, хотя бы одну цифру, строчную и заглавную букву (латиница)"
    )
    private String newPassword;

    @NotBlank(message = "Подтверждение пароля обязательно")
    private String confirmNewPassword;
}
