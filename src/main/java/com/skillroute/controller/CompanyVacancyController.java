package com.skillroute.controller;

import com.skillroute.dto.CreateVacancyRequest;
import com.skillroute.dto.UpdateVacancyRequest;
import com.skillroute.security.CustomUserDetails;
import com.skillroute.service.SkillService;
import com.skillroute.service.SpecializationService;
import com.skillroute.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/company/vacancies")
@RequiredArgsConstructor
public class CompanyVacancyController {
    private final SpecializationService specializationService;
    private final VacancyService vacancyService;
    private final SkillService skillService;

    @GetMapping
    public String vacanciesPage(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("vacancies", vacancyService.getVacanciesByCompany(user.getId()));
            return "company/vacancies";
        }

    @GetMapping("/create")
    public String createVacancyForm(Model model) {
        model.addAttribute("vacancy", new CreateVacancyRequest());
        model.addAttribute("specializations", specializationService.getSpecializations());
        model.addAttribute("skills", skillService.getSkills());
        return "company/create-vacancy";
    }

    @PostMapping("/create")
    public String createVacancy(@ModelAttribute CreateVacancyRequest form, @AuthenticationPrincipal CustomUserDetails user) {
        vacancyService.createVacancy(form, user.getId());
        return "redirect:/company/vacancies";
    }

    @GetMapping("/{id}/update")
    public String updateVacancyForm(@PathVariable Long id, Model model) {
        model.addAttribute("vacancy", vacancyService.getVacancyById(id));
        model.addAttribute("specializations", specializationService.getSpecializations());
        return "company/update-vacancy";
    }

    @PostMapping("/{id}/update")
    public String updateVacancy(@PathVariable Long id,
                                @ModelAttribute UpdateVacancyRequest form,
                                @AuthenticationPrincipal CustomUserDetails user,
                                RedirectAttributes redirectAttributes) {
        vacancyService.updateVacancy(id, form, user.getId());

        redirectAttributes.addFlashAttribute("message", "Изменения сохранены!");
        return "redirect:/company/vacancies/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteVacancy(@PathVariable Long id,
                                @AuthenticationPrincipal CustomUserDetails user,
                                RedirectAttributes redirectAttributes) {
        vacancyService.deleteVacancy(id, user.getId());

        redirectAttributes.addFlashAttribute("message", "Вакансия удалена");
        return "redirect:/company/vacancies";
    }
}
