package com.skillroute.controller;

import com.skillroute.dto.EditCompanyDto;
import com.skillroute.dto.EditStudentDto;
import com.skillroute.model.Account;
import com.skillroute.model.Role;
import com.skillroute.service.AccountService;
import com.skillroute.service.CompanyProfileService;
import com.skillroute.service.StudentProfileService;
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
@RequestMapping("/profile/edit")
@RequiredArgsConstructor
public class EditProfileController {
    private final AccountService accountService;
    private final CompanyProfileService companyProfileService;
    private final StudentProfileService studentProfileService;

    @GetMapping
    public String editProfilePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Account account = accountService.getAccount(userDetails.getUsername());
        model.addAttribute("account", account);
        model.addAttribute("profile", account.getRole() == Role.STUDENT ? account.getStudentProfile() : account.getCompanyProfile());
        return "edit_profile";
    }

    @PostMapping("/student")
    public String editStudent(@AuthenticationPrincipal UserDetails userDetails, @ModelAttribute EditStudentDto form) {
        Account account = accountService.getAccount(userDetails.getUsername());
        studentProfileService.updateProfile(account.getId(), form);
        return "redirect:/profile";
    }

    @PostMapping("/company")
    public String editCompany(@AuthenticationPrincipal UserDetails userDetails, @ModelAttribute EditCompanyDto form) {
        Account account = accountService.getAccount(userDetails.getUsername());
        companyProfileService.updateProfile(account.getId(), form);
        return "redirect:/profile";
    }
}
