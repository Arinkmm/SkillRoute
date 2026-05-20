package com.skillroute.mapper;

import com.skillroute.dto.response.StudentSkillResponse;
import com.skillroute.model.Skill;
import com.skillroute.model.StudentSkill;
import com.skillroute.model.id.StudentSkillId;
import com.skillroute.openapi.model.StudentSkillResponseApi;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StudentSkillMapperTest {
    private final StudentSkillMapper mapper = new StudentSkillMapper();

    @Test
    void mapsStudentSkillToDtoAndApiDto() {
        StudentSkill studentSkill = StudentSkill.builder()
                .id(new StudentSkillId(1L, 10L))
                .skill(Skill.builder().id(10L).name("Java").build())
                .level(4)
                .isConfirmedByGitHub(true)
                .build();

        StudentSkillResponse response = mapper.toResponse(studentSkill);
        StudentSkillResponseApi apiResponse = mapper.toResponseApi(studentSkill);

        assertThat(response.getSkillId()).isEqualTo(10L);
        assertThat(response.getName()).isEqualTo("Java");
        assertThat(response.getLevel()).isEqualTo(4);
        assertThat(response.isConfirmedByGitHub()).isTrue();

        assertThat(apiResponse.getSkillId()).isEqualTo(10L);
        assertThat(apiResponse.getName()).isEqualTo("Java");
        assertThat(apiResponse.getLevel()).isEqualTo(4);
        assertThat(apiResponse.getConfirmedByGitHub()).isTrue();
    }
}
