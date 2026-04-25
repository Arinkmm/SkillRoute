package com.skillroute.controller;

import com.skillroute.dto.SkillAddDto;
import com.skillroute.dto.StudentSkillDto;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/profile/skills")
@RequiredArgsConstructor
public class StudentSkillController {
    private final StudentSkillService studentSkillService;
    private final AccountService accountService;
    private final SkillService skillService;

    @GetMapping("/search")
    @ResponseBody
    public List<StudentSkillDto> searchSkills(@RequestParam("name") String name) {
        return studentSkillService.searchSkillsByName(name);
    }

    @GetMapping("/add")
    public String addSkillPage(Model model) {
        model.addAttribute("form", new SkillAddDto());
        model.addAttribute("skills", skillService.getSkills());
        return "add_skill";
    }

    @PostMapping("/add")
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
