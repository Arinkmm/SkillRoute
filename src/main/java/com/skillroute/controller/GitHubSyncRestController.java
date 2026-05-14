package com.skillroute.controller;

import com.skillroute.dto.response.ErrorResponse;
import com.skillroute.dto.response.GitHubSyncResponse;
import com.skillroute.security.CustomUserDetails;
import com.skillroute.service.GitHubSyncTaskService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student/skills/github-sync")
@RequiredArgsConstructor
@Tag(name = "Синхронизация навыков с GitHub", description = "Фоновое обновление навыков студента через анализ публичного профиля GitHub")
public class GitHubSyncRestController {
    private final GitHubSyncTaskService syncTaskService;

    @Operation(
            summary = "Запуск фоновой синхронизации навыков",
            description = "Запускает проверку GitHub в фоне и сразу возвращает текущий статус"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Синхронизация запущена или уже выполняется",
                    content = @Content(
                            schema = @Schema(implementation = GitHubSyncResponse.class),
                            examples = @ExampleObject(value = "{\"message\": \"Синхронизация GitHub запущена\", \"confirmedCount\": 0, \"status\": \"RUNNING\", \"running\": true}"))),
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
        return ResponseEntity.ok(syncTaskService.start(user.getId()));
    }

    @Operation(
            summary = "Статус синхронизации навыков",
            description = "Возвращает текущий статус фоновой проверки GitHub"
    )
    @ApiResponse(responseCode = "200", description = "Текущий статус",
            content = @Content(
                    schema = @Schema(implementation = GitHubSyncResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"Синхронизация с GitHub прошла успешно!\", \"confirmedCount\": 3, \"status\": \"SUCCESS\", \"running\": false}")))
    @GetMapping("/status")
    public ResponseEntity<GitHubSyncResponse> getStatus(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(syncTaskService.getStatus(user.getId()));
    }
}
