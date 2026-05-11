package com.skillroute.controller;

import com.skillroute.dto.request.ForgotPasswordRequest;
import com.skillroute.dto.request.ResetPasswordRequest;
import com.skillroute.properties.MessageProperties;
import com.skillroute.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/password")
@RequiredArgsConstructor
public class PasswordResetController {
    private final PasswordResetService passwordResetService;
    private final MessageProperties messages;

    @GetMapping("/forgot")
    public String forgotPasswordPage(Model model) {
        model.addAttribute("forgotPasswordForm", new ForgotPasswordRequest());
        return "forgot-password";
    }

    @PostMapping("/forgot")
    public String requestPasswordReset(@Valid @ModelAttribute ForgotPasswordRequest form, RedirectAttributes redirectAttributes) {
        passwordResetService.requestPasswordReset(form);
        redirectAttributes.addFlashAttribute("successMessage", messages.getPasswordReset().getEmailSent());
        return "redirect:/login";
    }

    @GetMapping("/reset")
    public String resetPasswordPage(@RequestParam String token, Model model) {
        passwordResetService.validatePasswordResetToken(token);
        model.addAttribute("resetPasswordForm", ResetPasswordRequest.builder().token(token).build());
        return "reset-password";
    }

    @PostMapping("/reset")
    public String resetPassword(@Valid @ModelAttribute ResetPasswordRequest form, RedirectAttributes redirectAttributes) {
        passwordResetService.resetPassword(form);
        redirectAttributes.addFlashAttribute("successMessage", messages.getPasswordReset().getSuccess());
        return "redirect:/login";
    }
}
