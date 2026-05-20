package com.skillroute.mapper;

import com.skillroute.dto.response.SkillGapResponse;
import com.skillroute.dto.response.StudentGapResponse;
import com.skillroute.dto.response.StudentPreviewResponse;
import com.skillroute.dto.response.StudentSkillResponse;
import com.skillroute.model.Skill;
import com.skillroute.model.StudentProfile;
import com.skillroute.model.StudentVacancyStatus;
import com.skillroute.model.Vacancy;
import com.skillroute.model.VacancySkill;
import com.skillroute.model.id.VacancySkillId;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicantMapperTest {
    private final ApplicantMapper mapper = new ApplicantMapper();

    @Test
    void mapsSkillGapStudentGapAndPreview() {
        Vacancy vacancy = Vacancy.builder().id(100L).build();
        Skill skill = Skill.builder().id(10L).name("Spring").build();
        VacancySkill vacancySkill = VacancySkill.builder()
                .id(new VacancySkillId(100L, 10L))
                .vacancy(vacancy)
                .skill(skill)
                .level(4)
                .build();
        StudentProfile student = StudentProfile.builder()
                .id(1L)
                .firstName("Maria")
                .lastName("Ivanova")
                .build();
        StudentSkillResponse mappedSkill = StudentSkillResponse.builder()
                .skillId(10L)
                .name("Spring")
                .level(2)
                .build();

        SkillGapResponse gap = mapper.toSkillGapResponse(vacancySkill, 2, 2);
        StudentGapResponse studentGap = mapper.toStudentGapResponse(
                student,
                66.7,
                2,
                StudentVacancyStatus.INTERVIEW,
                List.of(gap),
                List.of(mappedSkill));
        StudentPreviewResponse preview = mapper.toStudentPreviewResponse(student, studentGap);

        assertThat(gap.getSkillId()).isEqualTo(10L);
        assertThat(gap.getSkillName()).isEqualTo("Spring");
        assertThat(gap.getCurrentLevel()).isEqualTo(2);
        assertThat(gap.getTargetLevel()).isEqualTo(4);
        assertThat(gap.getGapDepth()).isEqualTo(2);

        assertThat(studentGap.getStudentId()).isEqualTo(1L);
        assertThat(studentGap.getGaps()).containsExactly(gap);
        assertThat(studentGap.getSkills()).containsExactly(mappedSkill);
        assertThat(studentGap.getStatus()).isEqualTo(StudentVacancyStatus.INTERVIEW);

        assertThat(preview.getStudentId()).isEqualTo(1L);
        assertThat(preview.getMatchPercentage()).isEqualTo(66.7);
        assertThat(preview.getTotalGapLevel()).isEqualTo(2);
        assertThat(preview.getStatus()).isEqualTo(StudentVacancyStatus.INTERVIEW);
    }
}
