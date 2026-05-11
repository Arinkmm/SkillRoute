package com.skillroute.controller;

import com.skillroute.dto.request.VacancyFilter;
import com.skillroute.properties.MessageProperties;
import com.skillroute.security.CustomUserDetails;
import com.skillroute.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/student/vacancies")
@RequiredArgsConstructor
public class StudentVacancyController {
    private final VacancyService vacancyService;
    private final RecommendationService recommendationService;
    private final StudentVacancyService studentVacancyService;
    private final RoadmapService roadmapService;
    private final MessageProperties messages;

    @GetMapping
    public String vacanciesPage(@AuthenticationPrincipal CustomUserDetails user, @Valid @ModelAttribute VacancyFilter filter, Model model) {
        model.addAttribute("recommendedVacancies", recommendationService.getRecommendedVacanciesForStudent(user.getId(), filter));
        model.addAttribute("hotVacancies", vacancyService.getHighDemandVacancies(5));
        model.addAttribute("allVacancies", vacancyService.getAllActive());

        model.addAttribute("filter", filter);

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
    public String applyToVacancy(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user, RedirectAttributes redirectAttributes) {
        studentVacancyService.applyToVacancy(user.getId(), id);
        redirectAttributes.addFlashAttribute("message", messages.getUi().getVacancyApplied());
        return "redirect:/student/vacancies/" + id;
    }
}
