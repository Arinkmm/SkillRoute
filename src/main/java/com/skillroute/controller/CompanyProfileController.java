package com.skillroute.controller;

import com.skillroute.security.CustomUserDetails;
import com.skillroute.service.CompanyProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/company/profile")
@RequiredArgsConstructor
public class CompanyProfileController {
    private final CompanyProfileService companyProfileService;

    @GetMapping
    public String profilePage(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("profile", companyProfileService.getCompanyById(user.getId()));
        return "company/profile";
    }
}
