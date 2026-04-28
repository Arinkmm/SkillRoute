package com.skillroute.controller;

import com.skillroute.model.Account;
import com.skillroute.model.CompanyProfile;
import com.skillroute.model.Role;
import com.skillroute.model.StudentProfile;
import com.skillroute.security.CustomUserDetails;
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
    @GetMapping
    public String mainPage(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("account", user);
        model.addAttribute("role", user.getRole());

        boolean isNewAccount = checkIsNew(user);
        boolean isConfirmed = checkIsConfirmed(user);

        model.addAttribute("isNewAccount", isNewAccount);
        model.addAttribute("isConfirmed", isConfirmed);

        return "main";
    }

    private boolean checkIsNew(CustomUserDetails user) {
        if (user.getRole() == Role.STUDENT) {
            StudentProfile profile = user.getAccount().getStudentProfile();
            return profile == null || profile.getFirstName() == null;
        }
        CompanyProfile profile = user.getAccount().getCompanyProfile();
        return profile == null || profile.getCompanyName() == null;
    }

    private boolean checkIsConfirmed(CustomUserDetails user) {
        return user.getRole() == Role.COMPANY &&
                user.getAccount().getCompanyProfile() != null &&
                user.getAccount().getCompanyProfile().isConfirmed();
    }
}
