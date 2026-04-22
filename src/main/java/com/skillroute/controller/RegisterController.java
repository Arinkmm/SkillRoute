package com.skillroute.controller;

import com.skillroute.dto.RegistrationDto;
import com.skillroute.exception.UserAlreadyExistsException;
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

    @GetMapping
    public String registrationPage(Model model) {
        model.addAttribute("form", new RegistrationDto());
        model.addAttribute("roles", Role.values());
        return "register";
    }

    @PostMapping
    public String processRegistration(@ModelAttribute RegistrationDto form,
                                      Model model) {
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            model.addAttribute("error", "Пароли не совпадают");
            return "register";
        }

        try {
            accountService.register(form);
        } catch (UserAlreadyExistsException e) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("error", "Пользователь с таким Email уже существует");
            return "register";
        }

        return "redirect:/login";
    }
}
