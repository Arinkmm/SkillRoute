package com.skillroute.controller;

import com.skillroute.dto.request.ResendEmailRequest;
import com.skillroute.properties.MessageProperties;
import com.skillroute.service.VerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/verification")
@RequiredArgsConstructor
public class VerificationController {
    private final VerificationService verificationService;
    private final MessageProperties messages;

    @GetMapping
    public String verificationPage(@RequestParam String token) {
        verificationService.verifyUser(token);
        return "verified";
    }

    @PostMapping("/resend")
    public String resendVerification(@Valid @ModelAttribute ResendEmailRequest form, RedirectAttributes redirectAttributes) {
        verificationService.resendVerificationEmail(form.getEmail());
        redirectAttributes.addFlashAttribute("successMessage", messages.getVerification().getResendSuccess());
        return "redirect:/login";
    }
}
