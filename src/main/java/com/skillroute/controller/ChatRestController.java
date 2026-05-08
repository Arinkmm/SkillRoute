package com.skillroute.controller;

import com.skillroute.dto.request.MessageRequest;
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
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "Мессенджер", description = "Операции по обмену сообщениями между пользователями")
public class ChatRestController {
    private final ChatService chatService;

    @Operation(summary = "Отправить сообщение")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Сообщение отправлено",
                    content = @Content(
                            schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(value = "{\"id\": 1, \"senderId\": 5, \"senderName\": \"Пользователь\", \"text\": \"Привет!\", \"createdAt\": \"2026-05-08T15:00:00\", \"isMine\": true}")
                    )),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации",
                    content = @Content(
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(value = "{\"text\": \"Сообщение не должно превышать 500 символов\"}")
                    )),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"message\": \"Произошла непредвиденная ошибка при отправке сообщения\", \"errorCode\": 500}")
                    ))
    })
    @PostMapping("/{id}/send")
    public ResponseEntity<MessageResponse> send(
            @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "ID пользователя-получателя", example = "42")
            @PathVariable Long id,
            @Valid @RequestBody MessageRequest request) {
        return ResponseEntity.ok(chatService.sendMessage(id, user.getId(), request));
    }
}