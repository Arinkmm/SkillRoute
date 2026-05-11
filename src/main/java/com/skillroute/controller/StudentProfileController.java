package com.skillroute.controller;

import com.skillroute.dto.request.EditPasswordRequest;
import com.skillroute.dto.request.UpdateStudentRequest;
import com.skillroute.properties.MessageProperties;
import com.skillroute.security.CustomUserDetails;
import com.skillroute.service.AccountService;
import com.skillroute.service.StudentProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("student/profile")
@RequiredArgsConstructor
public class StudentProfileController {
    private final StudentProfileService studentProfileService;
    private final AccountService accountService;
    private final MessageProperties messages;

    @GetMapping
    public String profilePage(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("profile", studentProfileService.getStudentById(user.getId()));
        return "student/profile";
    }

    @GetMapping("/update")
    public String updateProfilePage(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("account", user.getAccount());
        model.addAttribute("profile",user.getAccount().getStudentProfile());
        return "student/update-profile";
    }

    @PostMapping("/update")
    public String updateProfile(@AuthenticationPrincipal CustomUserDetails user,
                                @Valid @ModelAttribute UpdateStudentRequest form) {
        studentProfileService.updateProfile(user.getId(), form);
        return "redirect:/student/profile";
    }

    @GetMapping("/edit-password")
    public String editPasswordPage() {
        return "edit-password";
    }

    @PostMapping("/edit-password")
    public String editPassword(@AuthenticationPrincipal CustomUserDetails user,
                               @Valid @ModelAttribute EditPasswordRequest form,
                               RedirectAttributes redirectAttributes) {
        accountService.editPassword(user.getId(), form);
        redirectAttributes.addFlashAttribute("message", messages.getAccount().getPasswordUpdated());
        return "redirect:/student/profile";
    }
}
