package com.skillroute.controller;

import com.skillroute.openapi.model.RegistrationRequestApi;
import com.skillroute.openapi.model.ValidationResponseApi;
import com.skillroute.properties.MessageProperties;
import com.skillroute.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegisterRestController {
    private final RegistrationService registrationService;
    private final MessageProperties messages;

    @PostMapping("/check-field")
    public ResponseEntity<ValidationResponseApi> checkField(@Valid @RequestBody RegistrationRequestApi fieldData) {
        registrationService.validateRegistrationBusinessRules(fieldData);
        return ResponseEntity.ok(validationSuccess());
    }

    private ValidationResponseApi validationSuccess() {
        ValidationResponseApi response = new ValidationResponseApi();
        response.setValid(true);
        response.setMessage(messages.getValidationSuccess());
        return response;
    }
}
