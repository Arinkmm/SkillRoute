package com.skillroute.controller;

import com.skillroute.dto.request.RegistrationRequest;
import com.skillroute.model.Role;
import com.skillroute.properties.MessageProperties;
import com.skillroute.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegisterController {
    private final RegistrationService registrationService;
    private final MessageProperties messages;
    private static final List<Role> REGISTRATION_ROLES = List.of(Role.STUDENT, Role.COMPANY);

    @GetMapping
    public String registrationPage(Model model) {
        if (!model.containsAttribute("registrationForm")) {
            model.addAttribute("registrationForm", RegistrationRequest.builder().role(Role.STUDENT).build());
        }
        model.addAttribute("roles", REGISTRATION_ROLES);
        return "register";
    }

    @PostMapping
    public String register(@Valid @ModelAttribute RegistrationRequest form, RedirectAttributes redirectAttributes) {
        registrationService.register(form);
        redirectAttributes.addFlashAttribute("successMessage", messages.getRegistration().getSuccess());
        return "redirect:/login";
    }
}
