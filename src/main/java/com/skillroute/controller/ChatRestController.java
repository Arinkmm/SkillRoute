package com.skillroute.controller;

import com.skillroute.dto.request.MessageRequest;
import com.skillroute.dto.response.ChatResponse;
import com.skillroute.dto.response.ErrorResponse;
import com.skillroute.dto.response.MessageResponse;
import com.skillroute.security.CustomUserDetails;
import com.skillroute.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "Чат", description = "AJAX-операции для обмена сообщениями между студентом и компанией")
public class ChatRestController {
    private final ChatService chatService;

    @Operation(
            summary = "Получение сообщений чата",
            description = "Возвращает чат с собеседником и всеми сообщениями. Используется фронтендом для первичной загрузки и периодического обновления переписки."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Сообщения чата получены",
                    content = @Content(
                            schema = @Schema(implementation = ChatResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"chatId\": 12, \"opponentName\": \"ООО Ромашка\", \"messages\": [{\"id\": 101, \"senderId\": 5, \"senderName\": \"Анна Иванова\", \"text\": \"Здравствуйте! Готова обсудить вакансию.\", \"createdAt\": \"2026-05-15T12:30:00\", \"mine\": true}, {\"id\": 102, \"senderId\": 8, \"senderName\": \"ООО Ромашка\", \"text\": \"Здравствуйте! Когда вам удобно созвониться?\", \"createdAt\": \"2026-05-15T12:32:00\", \"mine\": false}]}"
                            )
                    )),
            @ApiResponse(
                    responseCode = "403",
                    description = "Пользователь не является участником чата",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"message\": \"У вас нет доступа к этому чату\", \"errorCode\": 403}"
                            )
                    )),
            @ApiResponse(
                    responseCode = "404",
                    description = "Чат не найден",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"message\": \"Чат не найден\", \"errorCode\": 404}"
                            )
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
    @GetMapping("/{id}/messages")
    public ResponseEntity<ChatResponse> messages(
            @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "ID чата", example = "12")
            @PathVariable Long id) {
        return ResponseEntity.ok(chatService.getChatResponse(id, user.getId()));
    }

    @Operation(
            summary = "Отправка сообщения",
            description = "Добавляет новое сообщение в чат от имени текущего пользователя и возвращает созданное сообщение для моментального добавления в интерфейс."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Сообщение отправлено",
                    content = @Content(
                            schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"id\": 103, \"senderId\": 5, \"senderName\": \"Анна Иванова\", \"text\": \"Сегодня после 16:00 удобно.\", \"createdAt\": \"2026-05-15T12:35:00\", \"mine\": true}"
                            )
                    )),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации тела запроса",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "blankText",
                                            summary = "Пустое сообщение",
                                            value = "{\"message\": \"Ошибка валидации\", \"errorCode\": 400, \"fields\": {\"text\": \"Текст сообщения не может быть пустым\"}}"
                                    ),
                                    @ExampleObject(
                                            name = "tooLongText",
                                            summary = "Сообщение длиннее 500 символов",
                                            value = "{\"message\": \"Ошибка валидации\", \"errorCode\": 400, \"fields\": {\"text\": \"Сообщение не должно превышать 500 символов\"}}"
                                    )
                            }
                    )),
            @ApiResponse(
                    responseCode = "403",
                    description = "Пользователь не является участником чата",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"message\": \"У вас нет доступа к этому чату\", \"errorCode\": 403}"
                            )
                    )),
            @ApiResponse(
                    responseCode = "404",
                    description = "Чат или аккаунт отправителя не найден",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "chatMissing",
                                            summary = "Чат не найден",
                                            value = "{\"message\": \"Чат не найден\", \"errorCode\": 404}"
                                    ),
                                    @ExampleObject(
                                            name = "accountMissing",
                                            summary = "Аккаунт не найден",
                                            value = "{\"message\": \"Аккаунт не найден\", \"errorCode\": 404}"
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
    @PostMapping("/{id}/send")
    public ResponseEntity<MessageResponse> send(
            @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "ID чата", example = "12")
            @PathVariable Long id,
            @Valid @RequestBody MessageRequest request) {
        return ResponseEntity.ok(chatService.sendMessage(id, user.getId(), request));
    }
}
