package com.skillroute.controller;

import com.skillroute.dto.request.EditPasswordRequest;
import com.skillroute.dto.response.ErrorResponse;
import com.skillroute.dto.response.ValidationResponse;
import com.skillroute.properties.MessageProperties;
import com.skillroute.security.CustomUserDetails;
import com.skillroute.service.AccountService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@Tag(name = "Аккаунт", description = "Проверка и изменение данных текущего аккаунта")
public class AccountRestController {
    private final AccountService accountService;
    private final MessageProperties messages;

    @Operation(
            summary = "Проверка полей смены пароля",
            description = "Валидирует данные формы смены пароля без сохранения нового пароля. Используется для UI-подсказок."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Данные валидны",
                    content = @Content(
                            schema = @Schema(implementation = ValidationResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"valid\": true, \"message\": \"Данные корректны\"}"
                            )
                    )),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка бизнес-валидации",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = "{\"message\": \"Ошибка изменения пароля\", \"errorCode\": 400, \"fields\": {\"oldPassword\": \"Текущий пароль введен неверно\", \"confirmNewPassword\": \"Новый пароль и подтверждение не совпадают\", \"newPassword\": \"Новый пароль не может быть таким же, как старый\"}}"
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
    @PostMapping("/password/check-field")
    public ResponseEntity<ValidationResponse> checkPasswordFields(@AuthenticationPrincipal CustomUserDetails user, @RequestBody EditPasswordRequest fieldData) {
        accountService.validateEditPasswordBusinessRules(user.getId(), fieldData);

        return ResponseEntity.ok(ValidationResponse.builder().valid(true).message(messages.getValidationSuccess()).build());
    }
}
