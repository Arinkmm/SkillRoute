package com.skillroute.controller;

import com.skillroute.dto.SkillAddDto;
import com.skillroute.dto.StudentSkillDto;
import com.skillroute.exception.DuplicateEntityException;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.model.Account;
import com.skillroute.model.Role;
import com.skillroute.security.CustomUserDetails;
import com.skillroute.service.AccountService;
import com.skillroute.service.SkillService;
import com.skillroute.service.StudentSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/student/skills")
@RequiredArgsConstructor
public class StudentSkillController {
    private final StudentSkillService studentSkillService;
    private final SkillService skillService;

    @GetMapping
    public String skillsPage(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("mySkills", studentSkillService.getStudentSkills(user.getId()));
        return "student/skills";
    }

    @GetMapping("/add")
    public String addSkillPage(Model model) {
        model.addAttribute("form", new SkillAddDto());
        model.addAttribute("skills", skillService.getSkills());
        return "student/add-skill";
    }

    @PostMapping("/add")
    public String addSkill(@ModelAttribute SkillAddDto form, @AuthenticationPrincipal CustomUserDetails user, RedirectAttributes redirectAttributes) {
        studentSkillService.addSkillToStudent(user.getId(), form);
        redirectAttributes.addFlashAttribute("success", "Навык успешно добавлен!");
        return "redirect:/student/skills";
    }
}
