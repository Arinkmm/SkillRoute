package com.skillroute.controller;

import com.skillroute.model.Role;
import com.skillroute.properties.MessageProperties;
import com.skillroute.security.CustomUserDetails;
import com.skillroute.service.CompanyProfileService;
import com.skillroute.service.StudentProfileService;
import com.skillroute.service.StudentVacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainController {
    private final CompanyProfileService companyProfileService;
    private final StudentProfileService studentProfileService;
    private final StudentVacancyService studentVacancyService;
    private final MessageProperties messages;

    @GetMapping
    public String mainPage(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("account", user);
        model.addAttribute("role", user.getRole());

        boolean isNewAccount = checkIsNew(user);
        boolean isConfirmed = checkIsConfirmed(user);

        model.addAttribute("isNewAccount", isNewAccount);
        model.addAttribute("isConfirmed", isConfirmed);

        if (user.getRole() == Role.ADMIN) {
            model.addAttribute("companies", companyProfileService.getAllCompanies());
        } else if (user.getRole() == Role.STUDENT && !isNewAccount) {
            model.addAttribute("followedVacancies", studentVacancyService.getFollowedVacancies(user.getId()));
        } else if (user.getRole() == Role.COMPANY && !isNewAccount && isConfirmed) {
            model.addAttribute("trackedStudents", studentVacancyService.getTrackedStudentsForCompany(user.getId()));
        }

        return "main";
    }

    @PostMapping("/companies/{id}/approve")
    public String approveCompany(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        companyProfileService.approveCompany(id);
        redirectAttributes.addFlashAttribute("message", messages.getUi().getCompanyApproved());

        return "redirect:/main";
    }

    @PostMapping("/companies/{id}/reject")
    public String rejectCompany(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        companyProfileService.rejectCompany(id);
        redirectAttributes.addFlashAttribute("message", messages.getUi().getCompanyRejected());

        return "redirect:/main";
    }

    private boolean checkIsNew(CustomUserDetails user) {
        if (user.getRole() == Role.STUDENT) {
            return !studentProfileService.isProfileComplete(user.getId());
        }
        if (user.getRole() == Role.ADMIN) {
            return false;
        }
        return !companyProfileService.isProfileComplete(user.getId());
    }

    private boolean checkIsConfirmed(CustomUserDetails user) {
        return user.getRole() == Role.COMPANY && companyProfileService.isConfirmed(user.getId());
    }
}
