package com.skillroute.controller;

import com.skillroute.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/verification")
@RequiredArgsConstructor
public class VerificationController {
    private final RegisterService registerService;

    @GetMapping
    public String verifyUser(@RequestParam("token") String token) {
        boolean isVerified = registerService.verifyUser(token);
        if (isVerified) {
            return "success_sign_up";
        } else {
            return "redirect:/login?error=invalid_token";
        }
    }
}
