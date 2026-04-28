package com.skillroute.controller;

import com.skillroute.model.Account;
import com.skillroute.security.CustomUserDetails;
import com.skillroute.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/student/vacancies")
@RequiredArgsConstructor
public class StudentVacancyController {
    private final VacancyService vacancyService;
    private final RecommendationService recommendationService;
    private final StudentVacancyService studentVacancyService;
    private final RoadmapService roadmapService;

    @GetMapping
    public String listVacancies(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("recommended", recommendationService.getRecommendedForStudent(user.getId()));
        model.addAttribute("all", vacancyService.getAllActive());
        return "student/vacancies";
    }

    @GetMapping("/{id}")
    public String viewVacancy(@PathVariable Long id,
                              @AuthenticationPrincipal CustomUserDetails user,
                              Model model) {
        model.addAttribute("vacancy", vacancyService.getVacancyById(id));
        model.addAttribute("roadmap", roadmapService.generateRoadmap(user.getId(), id));

        return "student/vacancy-details";
    }

    @PostMapping("/{id}/apply")
    public String apply(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user, RedirectAttributes ra) {
        studentVacancyService.applyToVacancy(user.getId(), id);
        ra.addFlashAttribute("message", "Отклик успешно отправлен!");
        return "redirect:/student/vacancies/" + id;
    }
}
