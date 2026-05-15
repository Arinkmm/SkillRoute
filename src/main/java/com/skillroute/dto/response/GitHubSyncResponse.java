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
@Schema(description = "Результат фоновой синхронизации навыков с GitHub")
public class GitHubSyncResponse {
    @Schema(description = "Сообщение для отображения пользователю", example = "Синхронизация GitHub запущена. Можно продолжать работу на сайте")
    private String message;

    @Schema(description = "Общее количество навыков, подтвержденных через GitHub", example = "5")
    private int confirmedCount;

    @Schema(description = "Статус фоновой синхронизации", example = "RUNNING")
    private GitHubSyncStatus status;

    @Schema(description = "Признак того, что синхронизация еще выполняется", example = "true")
    private boolean running;
}
