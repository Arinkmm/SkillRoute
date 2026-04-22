package com.skillroute.controller;

import com.skillroute.model.Account;
import com.skillroute.model.Role;
import com.skillroute.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainController {
    private final AccountService accountService;
    @GetMapping
    public String mainPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Account account = accountService.getAccount(userDetails.getUsername());
        model.addAttribute("account", account);
        model.addAttribute("role",  account.getRole());

        boolean isNewAccount = false;
        boolean isConfirmed = false;
        if (account.getRole() == Role.STUDENT) {
            isNewAccount = (account.getStudentProfile() == null || account.getStudentProfile().getFirstName() == null);
        } else if (account.getRole() == Role.COMPANY) {
            isNewAccount = (account.getCompanyProfile() == null || account.getCompanyProfile().getCompanyName() == null);
            isConfirmed = (account.getCompanyProfile() == null || account.getCompanyProfile().isConfirmed());
        }
        model.addAttribute("isConfirmed", isConfirmed);
        model.addAttribute("isNewAccount", isNewAccount);
        return "main";
    }
}
