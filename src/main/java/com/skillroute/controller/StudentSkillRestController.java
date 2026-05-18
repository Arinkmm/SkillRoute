package com.skillroute.controller;

import com.skillroute.openapi.model.StudentSkillResponseApi;
import com.skillroute.security.CustomUserDetails;
import com.skillroute.service.StudentSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/student/skills")
@RequiredArgsConstructor
public class StudentSkillRestController {
    private final StudentSkillService studentSkillService;

    @GetMapping("/search")
    public ResponseEntity<List<StudentSkillResponseApi>> searchSkills(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(value = "name", defaultValue = "") String name) {
        return ResponseEntity.ok(studentSkillService.getStudentsSkillsByName(user.getId(), name));
    }
}
