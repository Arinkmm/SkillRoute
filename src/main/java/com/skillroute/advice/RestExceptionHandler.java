package com.skillroute.advice;

import com.skillroute.exception.*;
import com.skillroute.openapi.model.ErrorResponseApi;
import com.skillroute.properties.MessageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice(annotations = RestController.class)
@Order(1)
public class RestExceptionHandler {
    private final MessageProperties messages;

    @ExceptionHandler(FieldValidationException.class)
    public ResponseEntity<ErrorResponseApi> handleFieldValidation(FieldValidationException e) {
        log.error("Ошибка бизнес-валидации формы: {}", e.getFields());
        return ResponseEntity.badRequest().body(error(e.getMessage(), HttpStatus.BAD_REQUEST, e.getFields()));
    }

    @ExceptionHandler(DataMappingException.class)
    public ResponseEntity<ErrorResponseApi> handleDataMappingException(DataMappingException e) {
        log.error("Ошибка маппинга: {}", e.getMessage());
        return ResponseEntity.badRequest().body(error(e.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(GithubUrlNotFoundException.class)
    public ResponseEntity<ErrorResponseApi> handleGithubUrlNotFoundException(GithubUrlNotFoundException e) {
        log.error("Отсутствует ссылка на Github профиль: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error(e.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponseApi> handleServiceUnavailable(ServiceUnavailableException e) {
        log.error("Внешний сервис недоступен: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseApi> handleEntityNotFound(EntityNotFoundException e) {
        log.error("Сущность не найдена: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error(e.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(ResourceOwnershipException.class)
    public ResponseEntity<ErrorResponseApi> handleResourceOwnership(ResourceOwnershipException e) {
        log.error("Попытка доступа к чужому ресурсу: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error(e.getMessage(), HttpStatus.FORBIDDEN));
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<ErrorResponseApi> handleDuplicate(DuplicateEntityException e) {
        log.error("Дубликат сущности: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error(e.getMessage(), HttpStatus.CONFLICT));
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ErrorResponseApi> handleTooManyRequests(TooManyRequestsException e) {
        log.error("Слишком частый запрос формы: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(error(e.getMessage(), HttpStatus.TOO_MANY_REQUESTS));
    }

    @ExceptionHandler(GitHubRateLimitException.class)
    public ResponseEntity<ErrorResponseApi> handleGitHubRateLimit(GitHubRateLimitException e) {
        log.error("Превышен лимит GitHub API: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfterSeconds(e)))
                .body(error(e.getMessage(), HttpStatus.TOO_MANY_REQUESTS));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseApi> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> a));

        log.error("Ошибки валидации DTO: {}", errors);
        return ResponseEntity.badRequest().body(error(messages.getValidationError(), HttpStatus.BAD_REQUEST, errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseApi> handleAll(Exception e) {
        log.error("Непредвиденное исключение в REST-слое: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(messages.getInternalServerError(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private ErrorResponseApi error(String message, HttpStatus status) {
        return error(message, status, null);
    }

    private long retryAfterSeconds(GitHubRateLimitException e) {
        LocalDateTime retryAfter = e.getRetryAfter();
        if (retryAfter == null) {
            return 1;
        }

        return Math.max(1, Duration.between(LocalDateTime.now(), retryAfter).toSeconds());
    }

    private ErrorResponseApi error(String message, HttpStatus status, Map<String, String> fields) {
        ErrorResponseApi response = new ErrorResponseApi();
        response.setMessage(message);
        response.setErrorCode(status.name());
        response.setFields(fields);
        return response;
    }
}
