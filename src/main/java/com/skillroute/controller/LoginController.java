package com.skillroute.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.Map;

@Controller
@RequestMapping("/login")
public class LoginController {
    @GetMapping
    public String loginPage(HttpServletRequest request, Model model) {
        String error = (String) request.getSession().getAttribute("loginError");

        if (error != null) {
            model.addAttribute("error", error);
            request.getSession().removeAttribute("loginError");
        }

        return "login";
    }
}
