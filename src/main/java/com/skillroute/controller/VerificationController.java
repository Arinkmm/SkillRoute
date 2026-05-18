package com.skillroute.controller;

import com.skillroute.properties.MessageProperties;
import com.skillroute.service.VerificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
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
    public String resendVerification(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String token = resolveExpiredVerificationToken(request);
        if (StringUtils.hasText(token)) {
            verificationService.resendVerificationEmailByToken(token);
            request.getSession().removeAttribute("expiredVerificationToken");
        } else {
            String email = resolveResendEmail(request);
            if (!StringUtils.hasText(email)) {
                redirectAttributes.addFlashAttribute("error", messages.getVerification().getEmailRequired());
                redirectAttributes.addFlashAttribute("showResendVerification", true);
                return "redirect:/login";
            }
            verificationService.resendVerificationEmail(email);
        }

        request.getSession().removeAttribute("pendingVerificationEmail");
        request.getSession().removeAttribute("unverifiedEmail");
        request.getSession().removeAttribute("expiredVerificationToken");

        redirectAttributes.addFlashAttribute("successMessage", messages.getVerification().getResendSuccess());
        return "redirect:/login";
    }

    private String resolveExpiredVerificationToken(HttpServletRequest request) {
        String token = request.getParameter("verificationToken");
        if (StringUtils.hasText(token)) {
            return token.trim();
        }

        Object sessionToken = request.getSession().getAttribute("expiredVerificationToken");
        return sessionToken instanceof String value && StringUtils.hasText(value) ? value.trim() : null;
    }

    private String resolveResendEmail(HttpServletRequest request) {
        Object pendingEmail = request.getSession().getAttribute("pendingVerificationEmail");
        if (pendingEmail instanceof String email && StringUtils.hasText(email)) {
            return email.trim();
        }

        Object unverifiedEmail = request.getSession().getAttribute("unverifiedEmail");
        if (unverifiedEmail instanceof String email && StringUtils.hasText(email)) {
            return email.trim();
        }

        String username = request.getParameter("username");
        return StringUtils.hasText(username) ? username.trim() : null;
    }
}
