package com.skillroute.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {
    @GetMapping
    public String loginPage(HttpServletRequest request, Model model) {
        String error = (String) request.getSession().getAttribute("loginError");
        String unverifiedEmail = (String) request.getSession().getAttribute("unverifiedEmail");
        boolean showResendVerification = false;

        if (error != null) {
            model.addAttribute("error", error);
            request.getSession().removeAttribute("loginError");
        }

        if (unverifiedEmail != null) {
            model.addAttribute("unverifiedEmail", unverifiedEmail);
            showResendVerification = true;
            request.getSession().removeAttribute("unverifiedEmail");
        }

        if (model.containsAttribute("verificationExpired")) {
            showResendVerification = true;
        }

        model.addAttribute("showResendVerification", showResendVerification);

        return "login";
    }
}
