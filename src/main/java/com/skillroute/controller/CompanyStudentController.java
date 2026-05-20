package com.skillroute.controller;

import com.skillroute.security.CustomUserDetails;
import com.skillroute.service.CompanyStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/company/students")
@RequiredArgsConstructor
public class CompanyStudentController {
    private final CompanyStudentService companyStudentService;

    @GetMapping
    public String studentsPage(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("trackedStudents", companyStudentService.getTrackedStudents(user.getId()));
        model.addAttribute("students", companyStudentService.getAvailableStudents(user.getId()));

        return "company/students";
    }

    @GetMapping("/{id}")
    public String studentDetails(@PathVariable Long id, Model model) {
        model.addAttribute("student", companyStudentService.getStudentDetails(id));

        return "company/student-profile";
    }
}
