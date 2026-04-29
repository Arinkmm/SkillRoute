package com.skillroute.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompanyRequest {
    @Size(max = 150, message = "Название компании не должно превышать 150 символов")
    private String companyName;

    @Size(max = 500, message = "Информация о компании не должна превышать 500 символов")
    private String description;

    @URL(message = "Должна быть корректная ссылка")
    @Size(max = 255)
    private String websiteUrl;
}
