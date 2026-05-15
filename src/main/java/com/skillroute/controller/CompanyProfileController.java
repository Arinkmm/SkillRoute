package com.skillroute.controller;

import com.skillroute.dto.request.EditPasswordRequest;
import com.skillroute.dto.request.UpdateCompanyRequest;
import com.skillroute.properties.MessageProperties;
import com.skillroute.security.CustomUserDetails;
import com.skillroute.service.AccountService;
import com.skillroute.service.CompanyProfileService;
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
@RequestMapping("/company/profile")
@RequiredArgsConstructor
public class CompanyProfileController {
    private final CompanyProfileService companyProfileService;
    private final AccountService accountService;
    private final MessageProperties messages;

    @GetMapping
    public String profilePage(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("profile", companyProfileService.getCompanyById(user.getId()));
        return "company/profile";
    }

    @GetMapping("/update")
    public String updateProfilePage(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("account", user.getAccount());
        model.addAttribute("updateCompanyForm", companyProfileService.getUpdateForm(user.getId()));
        return "company/update-profile";
    }

    @PostMapping("/update")
    public String updateProfile(@AuthenticationPrincipal CustomUserDetails user,
                                @Valid @ModelAttribute UpdateCompanyRequest form,
                                RedirectAttributes redirectAttributes) {
        companyProfileService.updateProfile(user.getId(), form);
        redirectAttributes.addFlashAttribute("message", messages.getAccount().getProfileUpdated());
        return "redirect:/company/profile";
    }

    @GetMapping("/edit-password")
    public String editPasswordPage(Model model) {
        model.addAttribute("editPasswordForm", new EditPasswordRequest());
        model.addAttribute("profilePath", "/company/profile");
        return "edit-password";
    }

    @PostMapping("/edit-password")
    public String editPassword(@AuthenticationPrincipal CustomUserDetails user,
                               @Valid @ModelAttribute EditPasswordRequest form,
                               RedirectAttributes redirectAttributes) {
        accountService.editPassword(user.getId(), form);
        redirectAttributes.addFlashAttribute("message", messages.getAccount().getPasswordUpdated());
        return "redirect:/company/profile";
    }

}
