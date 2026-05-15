package com.skillroute.advice;

import com.skillroute.dto.response.ErrorResponse;
import com.skillroute.exception.*;
import com.skillroute.properties.MessageProperties;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RestControllerAdvice(annotations = RestController.class)
@Order(1)
public class RestExceptionHandler {
    private final MessageProperties messages;

    @ExceptionHandler(FieldValidationException.class)
    public ResponseEntity<ErrorResponse> handleFieldValidation(FieldValidationException e) {
        log.error("Ошибка бизнес-валидации формы: {}", e.getFields());
        return ResponseEntity.badRequest().body(ErrorResponse.builder().message(e.getMessage()).errorCode(HttpStatus.BAD_REQUEST.value()).fields(e.getFields()).build());
    }

    @ExceptionHandler(DataMappingException.class)
    public ResponseEntity<ErrorResponse> handleDataMappingException(DataMappingException e) {
        log.error("Ошибка маппинга: {}", e.getMessage());
        return ResponseEntity.badRequest().body(ErrorResponse.builder().message(e.getMessage()).errorCode(HttpStatus.BAD_REQUEST.value()).build());
    }

    @ExceptionHandler(GithubUrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGithubUrlNotFoundException(GithubUrlNotFoundException e) {
        log.error("Отсутствует ссылка на Github профиль: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().message(e.getMessage()).errorCode(HttpStatus.NOT_FOUND.value()).build());
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailable(ServiceUnavailableException e) {
        log.error("Внешний сервис недоступен: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ErrorResponse.builder().message(e.getMessage()).errorCode(HttpStatus.SERVICE_UNAVAILABLE.value()).build());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException e) {
        log.error("Сущность не найдена: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().message(e.getMessage()).errorCode(HttpStatus.NOT_FOUND.value()).build());
    }

    @ExceptionHandler(ResourceOwnershipException.class)
    public ResponseEntity<ErrorResponse> handleResourceOwnership(ResourceOwnershipException e) {
        log.error("Попытка доступа к чужому ресурсу: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.builder().message(e.getMessage()).errorCode(HttpStatus.FORBIDDEN.value()).build());
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateEntityException e) {
        log.error("Дубликат сущности: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.builder().message(e.getMessage()).errorCode(HttpStatus.CONFLICT.value()).build());
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ErrorResponse> handleTooManyRequests(TooManyRequestsException e) {
        log.error("Слишком частый запрос формы: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ErrorResponse.builder().message(e.getMessage()).errorCode(HttpStatus.TOO_MANY_REQUESTS.value()).build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> a));

        log.error("Ошибки валидации DTO: {}", errors);
        return ResponseEntity.badRequest().body(ErrorResponse.builder().message(messages.getValidationError()).errorCode(HttpStatus.BAD_REQUEST.value()).fields(errors).build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception e) {
        log.error("Непредвиденное исключение в REST-слое: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder().message(messages.getInternalServerError()).errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());
    }
}
