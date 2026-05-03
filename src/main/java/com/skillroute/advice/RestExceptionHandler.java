package com.skillroute.advice;

import com.skillroute.dto.response.ErrorResponse;
import com.skillroute.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
@Order(1)
public class RestExceptionHandler {

    @ExceptionHandler({UserAlreadyExistsException.class, InvalidPasswordException.class})
    public ResponseEntity<ErrorResponse> handleRegistrationErrors(RuntimeException e) {
        log.warn("Предупреждение при регистрации: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder().message(e.getMessage()).errorCode(HttpStatus.BAD_REQUEST.value()).build());
    }

    @ExceptionHandler(AccountAlreadyVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleAccountAlreadyVerified(AccountAlreadyVerifiedException e) {
        log.warn("Попытка повторной верификации: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder().message(e.getMessage()).errorCode(HttpStatus.CONFLICT.value()).build());
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ErrorResponse> handleTooManyRequests(TooManyRequestsException e) {
        log.warn("Превышен лимит запросов (Rate Limit): {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ErrorResponse.builder().message(e.getMessage()).errorCode(HttpStatus.TOO_MANY_REQUESTS.value()).build());
    }

    @ExceptionHandler(MailSendMessageException.class)
    public ResponseEntity<ErrorResponse> handleMailSendMessageError(MailSendMessageException e) {
        log.error("Критическая ошибка почтового сервиса: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorResponse.builder().message(e.getMessage()).errorCode(HttpStatus.SERVICE_UNAVAILABLE.value()).build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> a));

        log.warn("Ошибки валидации DTO: {}", errors);
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception e) {
        log.error("Непредвиденное исключение в REST-слое: ", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .message("Произошла внутренняя ошибка сервера. Мы уже работаем над исправлением")
                        .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .build());
    }
}