package com.skillroute.controller;

import com.skillroute.dto.request.ApplicantFilter;
import com.skillroute.dto.response.StudentGapResponse;
import com.skillroute.dto.response.StudentPreviewResponse;
import com.skillroute.dto.response.VacancyResponse;
import com.skillroute.service.ApplicantService;
import com.skillroute.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/company/vacancies")
@RequiredArgsConstructor
public class ApplicantController {
    private final ApplicantService applicantService;
    private final VacancyService vacancyService;

    @GetMapping("/{id}/applicants")
    public String showApplicantsPage(
            @PathVariable Long id,
            @ModelAttribute ApplicantFilter filter,
            Model model) {
        VacancyResponse vacancy = vacancyService.getVacancyById(id);
        List<StudentPreviewResponse> applicants = applicantService.getFilteredApplicants(id, filter);

        model.addAttribute("vacancy", vacancy);
        model.addAttribute("applicants", applicants);

        return "company/applicants-list";
    }

    @GetMapping("/{vacancyId}/applicants/{studentId}")
    public String viewStudentDetails(@PathVariable Long vacancyId, @PathVariable Long studentId, Model model) {
        VacancyResponse vacancy = vacancyService.getVacancyById(vacancyId);
        StudentGapResponse gapDetail = applicantService.getStudentGap(studentId, vacancyId);

        model.addAttribute("gap", gapDetail);
        model.addAttribute("vacancy", vacancy);
        return "company/student-detail";
    }

    @PostMapping("/{vacancyId}/applicants/{studentId}/track")
    public String trackAndChat(@PathVariable Long vacancyId, @PathVariable Long studentId) {
        applicantService.startReviewing(studentId, vacancyId);

        return "redirect:/company/vacancies";
    }
}