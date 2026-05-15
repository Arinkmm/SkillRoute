package com.skillroute.controller;

import com.skillroute.dto.request.AddSkillRequest;
import com.skillroute.properties.MessageProperties;
import com.skillroute.security.CustomUserDetails;
import com.skillroute.service.SkillService;
import com.skillroute.service.StudentSkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/student/skills")
@RequiredArgsConstructor
public class StudentSkillController {
    private final StudentSkillService studentSkillService;
    private final SkillService skillService;
    private final MessageProperties messages;

    @GetMapping
    public String skillsPage(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("mySkills", studentSkillService.getStudentSkills(user.getId()));
        model.addAttribute("githubConfirmedCount", studentSkillService.countConfirmedByGitHub(user.getId()));
        return "student/skills";
    }

    @GetMapping("/add")
    public String addSkillForm(Model model) {
        model.addAttribute("addSkillForm", new AddSkillRequest());
        model.addAttribute("skills", skillService.getSkills());
        return "student/add-skill";
    }

    @PostMapping("/add")
    public String addSkill(@Valid @ModelAttribute AddSkillRequest form, @AuthenticationPrincipal CustomUserDetails user, RedirectAttributes redirectAttributes) {
        studentSkillService.addSkillToStudent(user.getId(), form);
        redirectAttributes.addFlashAttribute("success", messages.getUi().getStudentSkillAdded());
        return "redirect:/student/skills";
    }
}
