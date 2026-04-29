package com.skillroute.controller;

import com.skillroute.dto.request.RegistrationRequest;
import com.skillroute.dto.response.SuccessResponse;
import com.skillroute.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegisterRestController {
    private final AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse> register(@Valid @RequestBody RegistrationRequest form) {
        accountService.register(form);

        return ResponseEntity.ok(SuccessResponse.builder().message("Регистрация успешна").build());
    }
}