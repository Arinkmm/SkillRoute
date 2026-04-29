package com.skillroute.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddSkillRequest {
    private Long id;

    @Min(value = 1, message = "Уровень не может быть ниже 1")
    @Max(value = 5, message = "Уровень не может быть выше 5")
    private int level;
}
