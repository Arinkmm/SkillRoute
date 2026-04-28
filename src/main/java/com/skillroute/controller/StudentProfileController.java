package com.skillroute.controller;

import com.skillroute.model.Account;
import com.skillroute.model.Role;
import com.skillroute.security.CustomUserDetails;
import com.skillroute.service.AccountService;
import com.skillroute.service.StudentProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("student/profile")
@RequiredArgsConstructor
public class StudentProfileController {
    private final StudentProfileService studentProfileService;

    @GetMapping
    public String profilePage(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("profile", studentProfileService.getStudentById(user.getId()));
        return "student/profile";
    }
}
