package com.skillroute.controller;

import com.skillroute.dto.response.ErrorResponse;
import com.skillroute.dto.response.SuccessResponse;
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
@Tag(name = "Синхронизация навыков с GitHub", description = "Автоматизация обновления профиля через интеграцию с внешними API")
public class GitHubSyncRestController {
    private final GitHubSyncService syncService;
    private final MessageProperties messages;

    @Operation(
            summary = "Запуск синхронизации навыков",
            description = "Сканирует публичные репозитории пользователя на GitHub для обновления уровней владения технологиями"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успех",
                    content = @Content(
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = @ExampleObject(value = "{\"message\": \"Синхронизация с GitHub прошла успешно!\"}"))),
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
    public ResponseEntity<SuccessResponse> triggerSync(@AuthenticationPrincipal CustomUserDetails user) {
        syncService.syncSkills(user.getId());
        return ResponseEntity.ok(SuccessResponse.builder()
                .message(messages.getUi().getGithubSyncSuccess())
                .build());
    }
}
