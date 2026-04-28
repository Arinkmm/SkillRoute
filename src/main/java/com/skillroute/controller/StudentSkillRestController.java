package com.skillroute.controller;

import com.skillroute.dto.StudentSkillDto;
import com.skillroute.repository.SkillRepository;
import com.skillroute.service.SkillService;
import com.skillroute.service.StudentSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student/skills")
@RequiredArgsConstructor
public class StudentSkillRestController {
    private final SkillService skillService;

    @GetMapping("/search")
    public ResponseEntity<List<StudentSkillDto>> searchSkills(@RequestParam("name") String name) {
        return ResponseEntity.ok(skillService.searchSkillsByName(name));
    }
}
