package com.skillroute.controller;

import com.skillroute.dto.RoadmapResponseDto;
import com.skillroute.dto.VacancyResponseDto;
import com.skillroute.model.Account;
import com.skillroute.model.Role;
import com.skillroute.service.AccountService;
import com.skillroute.service.RoadmapService; // Добавляем новый сервис
import com.skillroute.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/vacancies")
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;
    private final AccountService accountService;
    private final RoadmapService roadmapService;

    @GetMapping
    public String vacanciesPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Account account = accountService.getAccount(userDetails.getUsername());

        if (account.getRole() == Role.STUDENT) {
            model.addAttribute("hasSpecialization", vacancyService.hasSpecialization(account.getId()));
            model.addAttribute("recommendedVacancies", vacancyService.getRecommendedForStudent(account.getId()));
            model.addAttribute("allVacancies", vacancyService.getAllActive());
            return "student/vacancies";
        } else {
            model.addAttribute("myVacancies", vacancyService.getVacanciesByCompany(account.getId()));
            return "company/vacancies";
        }
    }

    @GetMapping("/{id}")
    public String viewVacancy(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        Account account = accountService.getAccount(userDetails.getUsername());
        VacancyResponseDto vacancy = vacancyService.getVacancyById(id);
        model.addAttribute("vacancy", vacancy);

        if (account.getRole() == Role.STUDENT) {
            RoadmapResponseDto roadmap = roadmapService.generateRoadmap(account.getId(), id);
            model.addAttribute("roadmap", roadmap);

            return "student/vacancy-details";
        } else {
            return "company/vacancy-details";
        }
    }
}