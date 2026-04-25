package com.skillroute.controller;

import com.skillroute.dto.SkillResponseDto;
import com.skillroute.model.Account;
import com.skillroute.model.Role;
import com.skillroute.service.AccountService;
import com.skillroute.service.SkillService;
import com.skillroute.service.StudentSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;
    private final StudentSkillService studentSkillService;
    private final AccountService accountService;

    @GetMapping
    public String skillsPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Account account = accountService.getAccount(userDetails.getUsername());

        if (account.getRole() == Role.STUDENT) {
            model.addAttribute("mySkills", studentSkillService.getStudentSkills(account.getId()));
            return "student/skills";
        } else {
            model.addAttribute("allSkills", skillService.getSkills());
            return "company/skills";
        }
    }
}
