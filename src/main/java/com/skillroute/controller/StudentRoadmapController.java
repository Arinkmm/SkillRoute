package com.skillroute.controller;

import com.skillroute.dto.RoadmapResponseDto;
import com.skillroute.dto.RouteSkillDto;
import com.skillroute.dto.SkillAddDto;
import com.skillroute.exception.DuplicateEntityException;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.model.Account;
import com.skillroute.service.AccountService;
import com.skillroute.service.RoadmapService;
import com.skillroute.service.SkillService;
import com.skillroute.service.StudentSkillService;
import com.skillroute.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/route")
@RequiredArgsConstructor
public class StudentRoadmapController {
    private final RoadmapService roadmapService;
    private final VacancyService vacancyService;
    private final AccountService accountService;
    private final StudentSkillService studentSkillService;
    private final SkillService skillService;

    @GetMapping
    public String roadmapSelection(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Account account = accountService.getAccount(userDetails.getUsername());
        model.addAttribute("vacancies", vacancyService.getFollowedVacancies(account.getId()));
        return "student/roadmap-list";
    }

    @GetMapping("/{id}")
    public String viewRoadmap(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        Account account = accountService.getAccount(userDetails.getUsername());

        RoadmapResponseDto roadmap = roadmapService.generateRoadmap(account.getId(), id);

        model.addAttribute("roadmap", roadmap);

        return "student/roadmap-details";
    }

    @GetMapping("/{vacancyId}/skills/{skillId}")
    public String viewRoadmapSkillStep(@PathVariable Long vacancyId,
                                       @PathVariable Long skillId,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       Model model) {
        Account account = accountService.getAccount(userDetails.getUsername());

        RouteSkillDto skill = skillService.getRouteSkillById(skillId);

        model.addAttribute("vacancy", vacancyService.getVacancyById(vacancyId));
        model.addAttribute("skill", skill);

        boolean isAlreadyAcquired = studentSkillService.hasSkill(account.getId(), skillId);
        model.addAttribute("isAcquired", isAlreadyAcquired);

        return "student/roadmap-skill-step";
    }

    @PostMapping("/{vacancyId}/skills/{skillId}/acquire")
    public String acquireSkill(@PathVariable Long vacancyId,
                               @PathVariable Long skillId,
                               @ModelAttribute SkillAddDto addDto,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        Account account = accountService.getAccount(userDetails.getUsername());
        addDto.setId(skillId);
        try {
            studentSkillService.addSkillToStudent(account.getId(), addDto);
            redirectAttributes.addFlashAttribute("success", "Навык добавлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/route/" + vacancyId;
    }
}