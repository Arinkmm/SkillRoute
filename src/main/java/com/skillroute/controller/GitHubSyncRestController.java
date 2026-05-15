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
@Tag(
        name = "Синхронизация навыков с GitHub",
        description = "Фоновое подтверждение навыков студента через анализ публичного профиля GitHub"
)
public class GitHubSyncRestController {
    private final GitHubSyncTaskService syncTaskService;

    @Operation(
            summary = "Запуск фоновой синхронизации навыков",
            description = "Запускает проверку GitHub в фоне. Если синхронизация уже идет, возвращает текущий статус без запуска второго процесса."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Синхронизация запущена или уже выполняется",
                    content = @Content(
                            schema = @Schema(implementation = GitHubSyncResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "started",
                                            summary = "Синхронизация запущена",
                                            value = "{\"message\": \"Синхронизация GitHub запущена. Можно продолжать работу на сайте\", \"confirmedCount\": 2, \"status\": \"RUNNING\", \"running\": true}"
                                    ),
                                    @ExampleObject(
                                            name = "alreadyRunning",
                                            summary = "Синхронизация уже выполняется",
                                            value = "{\"message\": \"Синхронизация GitHub запущена. Можно продолжать работу на сайте\", \"confirmedCount\": 3, \"status\": \"RUNNING\", \"running\": true}"
                                    )
                            }
                    )),
            @ApiResponse(
                    responseCode = "404",
                    description = "GitHub URL или профиль студента не найден",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "githubUrlMissing",
                                            summary = "В профиле не указан GitHub",
                                            value = "{\"message\": \"Для анализа навыков необходимо указать GitHub URL в профиле\", \"errorCode\": 404}"
                                    ),
                                    @ExampleObject(
                                            name = "githubUrlInvalid",
                                            summary = "Некорректная ссылка GitHub",
                                            value = "{\"message\": \"Не удалось извлечь логин из некорректной ссылки GitHub: github.com\", \"errorCode\": 404}"
                                    ),
                                    @ExampleObject(
                                            name = "studentMissing",
                                            summary = "Профиль студента не найден",
                                            value = "{\"message\": \"Студент не найден\", \"errorCode\": 404}"
                                    )
                            }
                    )),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"message\": \"Произошла внутренняя ошибка сервера. Мы уже работаем над исправлением\", \"errorCode\": 500}"
                            )
                    ))
    })
    @PostMapping
    public ResponseEntity<GitHubSyncResponse> triggerSync(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(syncTaskService.start(user.getId()));
    }

    @Operation(
            summary = "Статус синхронизации навыков",
            description = "Возвращает текущий статус фоновой проверки GitHub и постоянное количество навыков, подтвержденных через GitHub."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Текущий статус синхронизации",
                    content = @Content(
                            schema = @Schema(implementation = GitHubSyncResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "idle",
                                            summary = "Синхронизация не запущена",
                                            value = "{\"message\": \"\", \"confirmedCount\": 4, \"status\": \"IDLE\", \"running\": false}"
                                    ),
                                    @ExampleObject(
                                            name = "running",
                                            summary = "Синхронизация выполняется",
                                            value = "{\"message\": \"Синхронизация GitHub запущена. Можно продолжать работу на сайте\", \"confirmedCount\": 5, \"status\": \"RUNNING\", \"running\": true}"
                                    ),
                                    @ExampleObject(
                                            name = "success",
                                            summary = "Синхронизация завершена",
                                            value = "{\"message\": \"Навыки успешно синхронизированы с GitHub\", \"confirmedCount\": 7, \"status\": \"SUCCESS\", \"running\": false}"
                                    ),
                                    @ExampleObject(
                                            name = "failed",
                                            summary = "Синхронизация завершилась ошибкой",
                                            value = "{\"message\": \"Лимит запросов GitHub исчерпан. Попробуйте позже\", \"confirmedCount\": 5, \"status\": \"FAILED\", \"running\": false}"
                                    )
                            }
                    )),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"message\": \"Произошла внутренняя ошибка сервера. Мы уже работаем над исправлением\", \"errorCode\": 500}"
                            )
                    ))
    })
    @GetMapping("/status")
    public ResponseEntity<GitHubSyncResponse> getStatus(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(syncTaskService.getStatus(user.getId()));
    }
}
