package com.skillroute.mapper;

import com.skillroute.dto.response.StudentSkillResponse;
import com.skillroute.model.StudentSkill;
import com.skillroute.openapi.model.StudentSkillResponseApi;
import org.springframework.stereotype.Component;

@Component
public class StudentSkillMapper {
    public StudentSkillResponseApi toResponseApi(StudentSkill studentSkill) {
        StudentSkillResponseApi response = new StudentSkillResponseApi();
        response.setSkillId(studentSkill.getSkill().getId());
        response.setName(studentSkill.getSkill().getName());
        response.setLevel(studentSkill.getLevel());
        response.setConfirmedByGitHub(studentSkill.isConfirmedByGitHub());
        return response;
    }

    public StudentSkillResponse toResponse(StudentSkill studentSkill) {
        return StudentSkillResponse.builder()
                .skillId(studentSkill.getSkill().getId())
                .name(studentSkill.getSkill().getName())
                .level(studentSkill.getLevel())
                .confirmedByGitHub(studentSkill.isConfirmedByGitHub())
                .build();
    }
}
