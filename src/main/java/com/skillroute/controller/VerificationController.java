package com.skillroute.controller;

import com.skillroute.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/verification")
@RequiredArgsConstructor
public class VerificationController {
    private final AccountService accountService;

    @GetMapping
    public String verifyUser(@RequestParam("token") String token) {
        boolean isVerified = accountService.verifyUser(token);
        if (isVerified) {
            return "verified";
        } else {
            return "redirect:/login?error=invalid_token";
        }
    }
}
