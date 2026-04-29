package com.skillroute.controller;

import com.skillroute.dto.response.StudentSkillResponse;
import com.skillroute.service.StudentSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student/skills")
@RequiredArgsConstructor
public class StudentSkillRestController {
    private final StudentSkillService studentSkillService;

    @GetMapping("/search")
    public ResponseEntity<List<StudentSkillResponse>> searchSkills(@RequestParam("name") String name) {
        return ResponseEntity.ok(studentSkillService.getStudentsSkillsByName(name));
    }
}
