package com.skillroute.controller;

import com.skillroute.openapi.model.ResetPasswordRequestApi;
import com.skillroute.openapi.model.ValidationResponseApi;
import com.skillroute.properties.MessageProperties;
import com.skillroute.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/password")
@RequiredArgsConstructor
public class PasswordResetRestController {
    private final PasswordResetService passwordResetService;
    private final MessageProperties messages;

    @PostMapping("/reset/check-field")
    public ResponseEntity<ValidationResponseApi> checkResetFields(@Valid @RequestBody ResetPasswordRequestApi fieldData) {
        passwordResetService.validateResetPasswordBusinessRules(fieldData);
        return ResponseEntity.ok(validationSuccess());
    }

    private ValidationResponseApi validationSuccess() {
        ValidationResponseApi response = new ValidationResponseApi();
        response.setValid(true);
        response.setMessage(messages.getValidationSuccess());
        return response;
    }
}
