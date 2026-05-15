package com.skillroute.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Статус фоновой синхронизации навыков с GitHub")
public enum GitHubSyncStatus {
    IDLE,
    RUNNING,
    SUCCESS,
    FAILED
}
