package com.skillroute.controller;

import com.skillroute.openapi.model.EditPasswordRequestApi;
import com.skillroute.openapi.model.ValidationResponseApi;
import com.skillroute.properties.MessageProperties;
import com.skillroute.security.CustomUserDetails;
import com.skillroute.service.AccountService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountRestController {
    private final AccountService accountService;
    private final MessageProperties messages;

    @PostMapping("/password/check-field")
    public ResponseEntity<ValidationResponseApi> checkPasswordFields(@AuthenticationPrincipal CustomUserDetails user,
                                                                     @Valid @RequestBody EditPasswordRequestApi fieldData) {
        accountService.validateEditPasswordBusinessRules(user.getId(), fieldData);
        return ResponseEntity.ok(validationSuccess());
    }

    private ValidationResponseApi validationSuccess() {
        ValidationResponseApi response = new ValidationResponseApi();
        response.setValid(true);
        response.setMessage(messages.getValidationSuccess());
        return response;
    }
}
