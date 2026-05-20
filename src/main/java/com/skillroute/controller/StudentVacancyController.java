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
    private final StudentVacancyCatalogService studentVacancyCatalogService;
    private final StudentVacancyService studentVacancyService;
    private final MessageProperties messages;

    @GetMapping
    public String vacanciesPage(@AuthenticationPrincipal CustomUserDetails user, @Valid @ModelAttribute VacancyFilter filter, Model model) {
        boolean filterApplied = studentVacancyCatalogService.isFilterApplied(filter);

        if (filterApplied) {
            model.addAttribute("filteredVacancies", studentVacancyCatalogService.getFilteredVacanciesForStudent(user.getId(), filter));
        } else {
            model.addAttribute("followedVacancies", studentVacancyCatalogService.getFollowedVacancies(user.getId()));
            model.addAttribute("recommendedVacancies", studentVacancyCatalogService.getRecommendedVacanciesForStudent(user.getId(), filter));
            model.addAttribute("hotVacancies", studentVacancyCatalogService.getHighDemandVacanciesForStudent(user.getId()));
            model.addAttribute("allVacancies", studentVacancyCatalogService.getAllActiveForStudent(user.getId()));
        }

        model.addAttribute("filter", filter);
        model.addAttribute("filterApplied", filterApplied);

        return "student/vacancies";
    }

    @GetMapping("/{id}")
    public String viewVacancy(@PathVariable Long id,
                              @AuthenticationPrincipal CustomUserDetails user,
                              Model model) {
        model.addAttribute("vacancy", vacancyService.getVacancyById(id));
        model.addAttribute("isTracked", studentVacancyService.isTracked(user.getId(), id));

        return "student/vacancy-details";
    }

    @PostMapping("/{id}/apply")
    public String applyToVacancy(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user, RedirectAttributes redirectAttributes) {
        studentVacancyService.applyToVacancy(user.getId(), id);
        redirectAttributes.addFlashAttribute("message", messages.getUi().getVacancyApplied());
        return "redirect:/student/vacancies/" + id;
    }
}
