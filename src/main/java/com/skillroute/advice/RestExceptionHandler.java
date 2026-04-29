package com.skillroute.advice;

import com.skillroute.dto.response.ErrorResponse;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.exception.InvalidPasswordException;
import com.skillroute.exception.UserAlreadyExistsException;
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

@RestControllerAdvice(annotations = RestController.class)
@Order(1)
public class RestExceptionHandler {

    @ExceptionHandler({UserAlreadyExistsException.class, InvalidPasswordException.class})
    public ResponseEntity<ErrorResponse> handleRegistrationErrors(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder().message(e.getMessage()).errorCode(HttpStatus.BAD_REQUEST.value()).build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .message("Произошла внутренняя ошибка сервера. Мы уже работаем над исправлением")
                        .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .build());
    }
}