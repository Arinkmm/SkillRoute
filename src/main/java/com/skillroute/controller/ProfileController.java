package com.skillroute.controller;

import com.skillroute.dto.EditCompanyDto;
import com.skillroute.model.Account;
import com.skillroute.model.Role;
import com.skillroute.service.AccountService;
import com.skillroute.service.CompanyProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final AccountService accountService;

    @GetMapping
    public String profilePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Account account = accountService.getAccount(userDetails.getUsername());
        model.addAttribute("account", account);

        if (account.getRole() == Role.STUDENT) {
            model.addAttribute("profile", account.getStudentProfile());
            return "student/profile";
        } else {
            model.addAttribute("profile", account.getCompanyProfile());
            return "company/profile";
        }
    }
}
