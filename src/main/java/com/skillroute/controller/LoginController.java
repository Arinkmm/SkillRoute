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
        Map<String, ?> flashAttributes = RequestContextUtils.getInputFlashMap(request);

        if (flashAttributes != null && flashAttributes.containsKey("error")) {
            model.addAttribute("error", flashAttributes.get("error"));
        }
        return "login";
    }
}
