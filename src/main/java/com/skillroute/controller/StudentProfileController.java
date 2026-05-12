package com.skillroute.controller;

import com.skillroute.dto.request.EditPasswordRequest;
import com.skillroute.dto.request.UpdateStudentRequest;
import com.skillroute.model.StudentProfile;
import com.skillroute.properties.MessageProperties;
import com.skillroute.security.CustomUserDetails;
import com.skillroute.service.AccountService;
import com.skillroute.service.SpecializationService;
import com.skillroute.service.StudentProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("student/profile")
@RequiredArgsConstructor
public class StudentProfileController {
    private final StudentProfileService studentProfileService;
    private final SpecializationService specializationService;
    private final AccountService accountService;
    private final MessageProperties messages;

    @GetMapping
    public String profilePage(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("account", user.getAccount());
        model.addAttribute("profile", studentProfileService.getStudentById(user.getId()));
        return "student/profile";
    }

    @GetMapping("/update")
    public String updateProfilePage(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("account", user.getAccount());
        model.addAttribute("profile", user.getAccount().getStudentProfile());
        model.addAttribute("specializations", specializationService.getSpecializations());
        model.addAttribute("updateStudentForm", buildStudentForm(user.getAccount().getStudentProfile()));
        return "student/update-profile";
    }

    @PostMapping("/update")
    public String updateProfile(@AuthenticationPrincipal CustomUserDetails user,
                                @Valid @ModelAttribute UpdateStudentRequest form,
                                RedirectAttributes redirectAttributes) {
        studentProfileService.updateProfile(user.getId(), form);
        redirectAttributes.addFlashAttribute("message", messages.getAccount().getProfileUpdated());
        return "redirect:/student/profile";
    }

    @GetMapping("/edit-password")
    public String editPasswordPage(Model model) {
        model.addAttribute("editPasswordForm", new EditPasswordRequest());
        model.addAttribute("profilePath", "/student/profile");
        return "edit-password";
    }

    @PostMapping("/edit-password")
    public String editPassword(@AuthenticationPrincipal CustomUserDetails user,
                               @Valid @ModelAttribute EditPasswordRequest form,
                               RedirectAttributes redirectAttributes) {
        accountService.editPassword(user.getId(), form);
        redirectAttributes.addFlashAttribute("message", messages.getAccount().getPasswordUpdated());
        return "redirect:/student/profile";
    }

    private UpdateStudentRequest buildStudentForm(StudentProfile profile) {
        if (profile == null) {
            return new UpdateStudentRequest();
        }

        return UpdateStudentRequest.builder()
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .gitHubUrl(profile.getGithubUrl())
                .specializationId(profile.getSpecialization() != null ? profile.getSpecialization().getId() : null)
                .bio(profile.getBio())
                .build();
    }
}
