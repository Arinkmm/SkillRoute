package com.skillroute.controller;

import com.skillroute.dto.SkillGapReport;
import com.skillroute.dto.VacancyCreateDto;
import com.skillroute.dto.VacancyResponseDto;
import com.skillroute.dto.VacancyUpdateDto;
import com.skillroute.model.Account;
import com.skillroute.model.Role;
import com.skillroute.model.Vacancy;
import com.skillroute.service.AccountService;
import com.skillroute.service.SpecializationService;
import com.skillroute.service.StudentVacancyService;
import com.skillroute.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vacancies")
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;
    private final StudentVacancyService studentVacancyService;
    private final AccountService accountService;
    private final SpecializationService specializationService;

    @GetMapping
    public String vacanciesPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Account account = accountService.getAccount(userDetails.getUsername());

        if (account.getRole() == Role.STUDENT) {
            boolean hasSpec = vacancyService.hasSpecialization(account.getId());
            model.addAttribute("hasSpecialization", hasSpec);

            model.addAttribute("recommendedVacancies", vacancyService.getRecommendedForStudent(account.getId()));
            model.addAttribute("allVacancies", vacancyService.getAllActive());
            return "student/vacancies";
        } else {
            model.addAttribute("myVacancies", vacancyService.getVacanciesByCompany(account.getId()));
            return "company/vacancies";
        }
    }

    @GetMapping("/{id}")
    public String viewVacancy(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        Account account = accountService.getAccount(userDetails.getUsername());
        VacancyResponseDto vacancy = vacancyService.getVacancyResponseById(id);

        model.addAttribute("vacancy", vacancy);

        if (account.getRole() == Role.STUDENT) {
            SkillGapReport skillGap = vacancyService.calculateSkillGap(account.getId(), id);
            model.addAttribute("skillGap", skillGap);
            return "student/vacancy-details";
        } else {
            return "company/vacancy-details";
        }
    }
}