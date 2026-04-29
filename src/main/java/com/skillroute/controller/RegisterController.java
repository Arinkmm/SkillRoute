package com.skillroute.controller;

import com.skillroute.dto.RegistrationRequest;
import com.skillroute.model.Role;
import com.skillroute.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegisterController {
    private final AccountService accountService;

    @ModelAttribute("roles")
    public Role[] getRoles() {
        return Role.values();
    }

    @GetMapping
    public String registrationPage(Model model) {
        model.addAttribute("form", new RegistrationRequest());
        model.addAttribute("roles", Role.values());
        return "register";
    }

    @PostMapping
    public String processRegistration(@ModelAttribute RegistrationRequest form) {
        accountService.register(form);
        return "redirect:/login";
    }
}
