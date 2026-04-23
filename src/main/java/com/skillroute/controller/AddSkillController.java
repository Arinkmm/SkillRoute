package com.skillroute.controller;

import com.skillroute.dto.SkillAddDto;
import com.skillroute.exception.DuplicateEntityException;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.model.Account;
import com.skillroute.service.AccountService;
import com.skillroute.service.SkillService;
import com.skillroute.service.StudentSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile/skills/add")
@RequiredArgsConstructor
public class AddSkillController {
    private final StudentSkillService studentSkillService;
    private final SkillService skillService;
    private final AccountService accountService;

    @GetMapping
    public String addSkillPage(Model model) {
        model.addAttribute("form", new SkillAddDto());
        model.addAttribute("skills", skillService.getSkills());
        return "add_skill";
    }

    @PostMapping
    public String addSkill(@ModelAttribute SkillAddDto form, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        Account account = accountService.getAccount(userDetails.getUsername());
        try {
            studentSkillService.addSkillToStudent(account.getId(), form);
            redirectAttributes.addFlashAttribute("success", "Навык успешно добавлен!");
        } catch (DuplicateEntityException | EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "add_skill";
        }
        return "redirect:/profile";
    }
}
