package com.skillroute.controller;

import com.skillroute.dto.StudentSkillDto;
import com.skillroute.service.StudentSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StudentSkillRestController {
    private final StudentSkillService studentSkillService;

    @GetMapping("profile/skills/search")
    @ResponseBody
    public List<StudentSkillDto> searchSkills(@RequestParam("name") String name) {
        return studentSkillService.searchSkillsByName(name);
    }
}
