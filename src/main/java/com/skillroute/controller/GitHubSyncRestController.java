package com.skillroute.controller;

import com.skillroute.dto.response.ErrorResponse;
import com.skillroute.dto.response.GitHubSyncResponse;
import com.skillroute.properties.MessageProperties;
import com.skillroute.security.CustomUserDetails;
import com.skillroute.service.GitHubSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student/skills/github-sync")
@RequiredArgsConstructor
@Tag(name = "Синхронизация навыков с GitHub", description = "Обновление навыков студента через анализ публичных репозиториев GitHub")
public class GitHubSyncRestController {
    private final GitHubSyncService syncService;
    private final MessageProperties messages;

    @Operation(
            summary = "Запуск синхронизации навыков",
            description = "Сканирует публичные репозитории пользователя на GitHub и возвращает количество подтвержденных навыков"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успех",
                    content = @Content(
                            schema = @Schema(implementation = GitHubSyncResponse.class),
                            examples = @ExampleObject(value = "{\"message\": \"Синхронизация с GitHub прошла успешно!\", \"confirmedCount\": 3}"))),
            @ApiResponse(responseCode = "404", description = "URL не найден",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"message\": \"Для анализа навыков необходимо указать GitHub URL в профиле\", \"errorCode\": 404}")
                    )),
            @ApiResponse(
                    responseCode = "503",
                    description = "Внешний сервис временно недоступен",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"message\": \"GitHub API недоступен\", \"errorCode\": 503}")
                    )),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"message\": \"Произошла непредвиденная ошибка при синхронизации\", \"errorCode\": 500}")
                    ))
    })
    @PostMapping
    public ResponseEntity<GitHubSyncResponse> triggerSync(@AuthenticationPrincipal CustomUserDetails user) {
        int confirmedCount = syncService.syncSkills(user.getId());
        String message = confirmedCount > 0 ? messages.getUi().getGithubSyncSuccess() : messages.getUi().getGithubSyncFailed();

        return ResponseEntity.ok(GitHubSyncResponse.builder()
                .message(message)
                .confirmedCount(confirmedCount)
                .build());
    }
}
