package com.skillroute.controller;

import com.skillroute.dto.EditCompanyDto;
import com.skillroute.dto.EditStudentDto;
import com.skillroute.model.Account;
import com.skillroute.model.Role;
import com.skillroute.security.CustomUserDetails;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile/edit")
@RequiredArgsConstructor
public class EditProfileController {
    private final StudentProfileService studentProfileService;
    private final CompanyProfileService companyProfileService;

    @GetMapping
    public String editProfilePage(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("account", user.getAccount());
        model.addAttribute("profile", user.getRole() == Role.STUDENT ?
                user.getAccount().getStudentProfile() : user.getAccount().getCompanyProfile());
        return "edit-profile";
    }

    @PostMapping("/student")
    public String editStudent(@AuthenticationPrincipal CustomUserDetails user,
                              @ModelAttribute EditStudentDto form) {
        studentProfileService.updateProfile(user.getId(), form);
        return "redirect:/profile";
    }

    @PostMapping("/company")
    public String editCompany(@AuthenticationPrincipal CustomUserDetails user,
                              @ModelAttribute EditCompanyDto form,
                              RedirectAttributes redirectAttributes) {
        companyProfileService.updateProfile(user.getId(), form);
        redirectAttributes.addFlashAttribute("message", "Профиль успешно обновлён");
        return "redirect:/profile";
    }
}
