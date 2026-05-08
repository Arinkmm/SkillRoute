package com.skillroute.controller;

import com.skillroute.dto.request.RegistrationRequest;
import com.skillroute.dto.request.ResendEmailRequest;
import com.skillroute.dto.response.ErrorResponse;
import com.skillroute.dto.response.SuccessResponse;
import com.skillroute.dto.response.ValidationResponse;
import com.skillroute.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
@Tag(name = "Registration", description = "Эндпоинты для процесса регистрации и верификации аккаунта")
public class RegisterRestController {
    private final AccountService accountService;

    @Operation(
            summary = "Проверка полей",
            description = "Валидирует данные формы (email, пароль) без создания аккаунта. Используется для UI-подсказок"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Данные валидны",
                    content = @Content(
                            schema = @Schema(implementation = ValidationResponse.class),
                            examples = @ExampleObject(
                                    name = "Success Validation",
                                    value = "{\"valid\": true, \"message\": \"Поле заполнено верно\"}"
                            )
                    )),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации",
                    content = @Content(
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(value = "{\"password\": \"Пароль должен содержать заглавную букву и цифру\"}")
                    )),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"message\": \"Произошла непредвиденная ошибка при проверке полей\", \"errorCode\": 500}")
                    ))
    })
    @PostMapping("/check-field")
    public ResponseEntity<ValidationResponse> checkField(@Valid @RequestBody RegistrationRequest fieldData) {
        return ResponseEntity.ok(ValidationResponse.builder().valid(true).message("Данные корректны").build());
    }

    @Operation(
            summary = "Повторная отправка письма",
            description = "Генерирует новый токен и отправляет письмо, если старое истекло"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Письмо отправлено",
                    content = @Content(examples = @ExampleObject(value = "{\"message\": \"Ссылка для подтверждения отправлена повторно\"}"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"message\": \"Пользователь с таким email не найден\", \"errorCode\": 404}")
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Ошибка почтового сервиса",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"message\": \"Ошибка отправки почты. Попробуйте позже\", \"errorCode\": 503}")
                    )),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"message\": \"Произошла непредвиденная ошибка при регистрации\", \"errorCode\": 500}")
                    ))
    })
    @PostMapping("/resend-verification")
    public ResponseEntity<SuccessResponse> resendVerification(@Valid @RequestBody ResendEmailRequest request) {
        accountService.resendVerificationEmail(request.getEmail());
        return ResponseEntity.ok(SuccessResponse.builder()
                .message("Ссылка для подтверждения отправлена повторно")
                .build());
    }
}