package com.skillroute.controller;

import com.skillroute.dto.request.RegistrationRequest;
import com.skillroute.dto.request.ResendEmailRequest;
import com.skillroute.dto.response.SuccessResponse;
import com.skillroute.service.AccountService;
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
public class RegisterRestController {
    private final AccountService accountService;

    @PostMapping("/check-field")
    public ResponseEntity<Map<String, String>> checkField(@Valid @RequestBody RegistrationRequest fieldData) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<SuccessResponse> resendVerification(@Valid @RequestBody ResendEmailRequest request) {
        accountService.resendVerificationEmail(request.getEmail());

        return ResponseEntity.ok(SuccessResponse.builder().message("Ссылка для подтверждения отправлена повторно").build());
    }
}