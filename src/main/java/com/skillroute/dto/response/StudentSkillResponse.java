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
@Schema(description = "Информация о навыке студента")
public class StudentSkillResponse {
    @Schema(description = "Уникальный идентификатор навыка", example = "101")
    private Long skillId;

    @Schema(description = "Название технологии или навыка", example = "Spring Boot")
    private String name;

    @Schema(description = "Уровень владения (1-5)", example = "3")
    private int level;

    @Schema(description = "Флаг подтверждения навыка через анализ GitHub", example = "true")
    private boolean isConfirmedByGitHub;
}
