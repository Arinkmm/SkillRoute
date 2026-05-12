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
@Schema(description = "Результат синхронизации навыков с GitHub")
public class GitHubSyncResponse {
    @Schema(description = "Текстовое сообщение о результате синхронизации", example = "Синхронизация с GitHub прошла успешно")
    private String message;

    @Schema(description = "Количество навыков, подтвержденных по GitHub", example = "3")
    private int confirmedCount;
}
