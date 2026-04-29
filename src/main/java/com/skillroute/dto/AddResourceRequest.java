package com.skillroute.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddResourceRequest {
    @NotBlank(message = "Ссылка на ресурс обязательна")
    @URL(message = "Укажите валидный URL ресурса")
    private String resource;
}