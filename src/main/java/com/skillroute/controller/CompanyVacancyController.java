package com.skillroute.controller;

import com.skillroute.dto.VacancyCreateDto;
import com.skillroute.dto.VacancyUpdateDto;
import com.skillroute.model.Account;
import com.skillroute.service.AccountService;
import com.skillroute.service.SkillService;
import com.skillroute.service.SpecializationService;
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
public class CompanyVacancyController {
    private final SpecializationService specializationService;
    private final VacancyService vacancyService;
    private final AccountService accountService;
    private final SkillService skillService;

    @GetMapping("/create")
    public String createVacancyForm(Model model) {
        model.addAttribute("vacancy", new VacancyCreateDto());
        model.addAttribute("specializations", specializationService.getSpecializations());
        model.addAttribute("skills", skillService.getSkills());
        return "company/vacancy-create";
    }

    @PostMapping("/create")
    public String saveVacancy(@ModelAttribute("vacancy") VacancyCreateDto form,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        Account account = accountService.getAccount(userDetails.getUsername());
        vacancyService.createVacancy(form, account.getId());

        redirectAttributes.addFlashAttribute("message", "Вакансия успешно опубликована!");
        return "redirect:/vacancies";
    }

    @GetMapping("/{id}/edit")
    public String editVacancyForm(@PathVariable Long id, Model model) {
        model.addAttribute("vacancy", vacancyService.getVacancyById(id));
        model.addAttribute("specializations", specializationService.getSpecializations());
        return "company/vacancy-edit";
    }

    @PostMapping("/{id}/edit")
    public String updateVacancy(@PathVariable Long id,
                                @ModelAttribute("vacancy") VacancyUpdateDto form,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {

        Account account = accountService.getAccount(userDetails.getUsername());
        vacancyService.updateVacancy(id, form, account.getId());

        redirectAttributes.addFlashAttribute("message", "Изменения сохранены!");
        return "redirect:/vacancies/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteVacancy(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        Account account = accountService.getAccount(userDetails.getUsername());
        vacancyService.deleteVacancy(id, account.getId());

        redirectAttributes.addFlashAttribute("message", "Вакансия удалена");
        return "redirect:/vacancies";
    }
}
