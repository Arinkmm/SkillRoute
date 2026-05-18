package com.skillroute.mapper;

import com.skillroute.dto.response.StudentSkillResponse;
import com.skillroute.model.StudentSkill;
import org.springframework.stereotype.Component;

@Component
public class StudentSkillMapper {

    public StudentSkillResponse toResponse(StudentSkill studentSkill) {
        return StudentSkillResponse.builder()
                .skillId(studentSkill.getSkill().getId())
                .name(studentSkill.getSkill().getName())
                .level(studentSkill.getLevel())
                .isConfirmedByGitHub(studentSkill.isConfirmedByGitHub())
                .build();
    }
}
