package com.skillroute.controller;

import com.skillroute.dto.SkillAddDto;
import com.skillroute.dto.StudentSkillDto;
import com.skillroute.exception.DuplicateEntityException;
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

    @GetMapping
    public String getSkillsPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Account account = accountService.getAccount(userDetails.getUsername());

        List<StudentSkillDto> studentSkills = studentSkillService.getStudentSkills(account.getId());
        model.addAttribute("studentSkills", studentSkills);

        return "skills";
    }

    @GetMapping("/search")
    @ResponseBody
    public List<StudentSkillDto> searchSkills(@RequestParam("name") String name) {
        return studentSkillService.searchSkillsByName(name);
    }
}
