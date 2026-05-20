package com.skillroute.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {
    @GetMapping
    public String loginPage(HttpServletRequest request, Model model) {
        moveSessionAttributeToModel(request, model, "loginError", "error");

        String unverifiedEmail = resolveUnverifiedEmail(request, model);

        String expiredVerificationToken = resolveExpiredVerificationToken(request, model);
        if (StringUtils.hasText(expiredVerificationToken)) {
            model.addAttribute("expiredVerificationToken", expiredVerificationToken.trim());
        }

        model.addAttribute("showResendVerification", shouldShowResendVerification(model, unverifiedEmail, expiredVerificationToken));

        return "login";
    }

    private void moveSessionAttributeToModel(HttpServletRequest request, Model model, String sessionAttribute, String modelAttribute) {
        Object value = request.getSession().getAttribute(sessionAttribute);
        if (value != null) {
            model.addAttribute(modelAttribute, value);
            request.getSession().removeAttribute(sessionAttribute);
        }
    }

    private String resolveUnverifiedEmail(HttpServletRequest request, Model model) {
        Object flashEmail = model.asMap().get("unverifiedEmail");
        if (flashEmail instanceof String email && StringUtils.hasText(email)) {
            request.getSession().setAttribute("pendingVerificationEmail", email.trim());
            request.getSession().removeAttribute("unverifiedEmail");
            return email.trim();
        }

        Object sessionEmail = request.getSession().getAttribute("unverifiedEmail");
        request.getSession().removeAttribute("unverifiedEmail");
        if (sessionEmail instanceof String email && StringUtils.hasText(email)) {
            request.getSession().setAttribute("pendingVerificationEmail", email.trim());
            return email.trim();
        }

        return null;
    }

    private String resolveExpiredVerificationToken(HttpServletRequest request, Model model) {
        Object flashToken = model.asMap().get("expiredVerificationToken");
        if (flashToken instanceof String token && StringUtils.hasText(token)) {
            request.getSession().setAttribute("expiredVerificationToken", token.trim());
            return token.trim();
        }

        Object sessionToken = request.getSession().getAttribute("expiredVerificationToken");
        return sessionToken instanceof String token ? token : null;
    }

    private boolean shouldShowResendVerification(Model model, String unverifiedEmail, String expiredVerificationToken) {
        return StringUtils.hasText(unverifiedEmail)
                || StringUtils.hasText(expiredVerificationToken)
                || Boolean.TRUE.equals(model.asMap().get("showResendVerification"))
                || model.containsAttribute("verificationExpired")
                || model.containsAttribute("validationErrors");
    }
}
