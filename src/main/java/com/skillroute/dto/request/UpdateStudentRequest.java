package com.skillroute.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStudentRequest {
    @Size(max = 100, message = "Имя не должно превышать 100 символов")
    private String firstName;

    @Size(max = 100, message = "Фамилия не должна превышать 100 символов")
    private String lastName;

    @Pattern(
            regexp = "^https?://(www\\.)?github\\.com/[a-zA-Z0-9-]+/?$",
            message = "Ссылка должна быть валидным URL профиля GitHub (например, https://github.com/username)"
    )
    private String gitHubUrl;

    private Long specializationId;

    @Size(max = 500, message = "Информация о себе не должна превышать 500 символов")
    private String bio;

}
