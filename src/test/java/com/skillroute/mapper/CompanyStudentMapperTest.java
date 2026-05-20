package com.skillroute.mapper;

import com.skillroute.dto.response.CompanyStudentDetailsResponse;
import com.skillroute.dto.response.CompanyStudentResponse;
import com.skillroute.dto.response.StudentSkillResponse;
import com.skillroute.model.Direction;
import com.skillroute.model.Language;
import com.skillroute.model.Skill;
import com.skillroute.model.Specialization;
import com.skillroute.model.StudentProfile;
import com.skillroute.model.StudentSkill;
import com.skillroute.model.StudentVacancy;
import com.skillroute.model.StudentVacancyStatus;
import com.skillroute.model.Vacancy;
import com.skillroute.model.id.StudentSkillId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CompanyStudentMapperTest {
    private final CompanyStudentMapper mapper = new CompanyStudentMapper(new StudentSkillMapper());

    @Test
    void toTrackedResponseMapsStudentSummaryStatusAndCounts() {
        StudentProfile student = student();
        StudentVacancy application = StudentVacancy.builder()
                .student(student)
                .vacancy(Vacancy.builder().id(100L).name("Backend").build())
                .status(StudentVacancyStatus.INTERVIEW)
                .build();

        CompanyStudentResponse response = mapper.toTrackedResponse(application);

        assertThat(response.getStudentId()).isEqualTo(1L);
        assertThat(response.getFirstName()).isEqualTo("Maria");
        assertThat(response.getSpecializationName()).isEqualTo("Frontend / TypeScript");
        assertThat(response.getVacancyId()).isEqualTo(100L);
        assertThat(response.getVacancyName()).isEqualTo("Backend");
        assertThat(response.getStatus()).isEqualTo(StudentVacancyStatus.INTERVIEW);
        assertThat(response.getSkillCount()).isEqualTo(2);
        assertThat(response.getConfirmedSkillCount()).isEqualTo(1);
    }

    @Test
    void toDetailsResponseKeepsProvidedSkillsAndProfileFields() {
        StudentProfile student = student();
        List<StudentSkillResponse> skills = List.of(StudentSkillResponse.builder()
                .skillId(10L)
                .name("JavaScript")
                .level(4)
                .confirmedByGitHub(true)
                .build());

        CompanyStudentDetailsResponse response = mapper.toDetailsResponse(student, skills);

        assertThat(response.getStudentId()).isEqualTo(1L);
        assertThat(response.getGithubUrl()).isEqualTo("https://github.com/maria");
        assertThat(response.getBio()).isEqualTo("Frontend developer");
        assertThat(response.getSkills()).isSameAs(skills);
    }

    @Test
    void formatsAllSpecializationDirectionsAndLanguages() {
        for (Direction direction : Direction.values()) {
            StudentProfile student = StudentProfile.builder()
                    .id(1L)
                    .firstName("Maria")
                    .lastName("Ivanova")
                    .specialization(Specialization.builder().direction(direction).build())
                    .studentSkills(Set.of())
                    .build();

            assertThat(mapper.toCatalogResponse(student).getSpecializationName()).isNotBlank();
        }

        for (Language language : Language.values()) {
            StudentProfile student = StudentProfile.builder()
                    .id(1L)
                    .firstName("Maria")
                    .lastName("Ivanova")
                    .specialization(Specialization.builder().language(language).build())
                    .studentSkills(Set.of())
                    .build();

            assertThat(mapper.toCatalogResponse(student).getSpecializationName()).isNotBlank();
        }

        assertThat(mapper.toCatalogResponse(StudentProfile.builder()
                .id(1L)
                .firstName("Maria")
                .lastName("Ivanova")
                .studentSkills(Set.of())
                .build()).getSpecializationName()).isNull();
    }

    private StudentProfile student() {
        Skill javascript = Skill.builder().id(10L).name("JavaScript").build();
        Skill css = Skill.builder().id(11L).name("CSS").build();
        StudentProfile student = StudentProfile.builder()
                .id(1L)
                .firstName("Maria")
                .lastName("Ivanova")
                .githubUrl("https://github.com/maria")
                .bio("Frontend developer")
                .specialization(Specialization.builder()
                        .direction(Direction.FRONTEND)
                        .language(Language.TYPESCRIPT)
                        .build())
                .build();

        student.setStudentSkills(Set.of(
                StudentSkill.builder()
                        .id(new StudentSkillId(1L, 10L))
                        .student(student)
                        .skill(javascript)
                        .level(4)
                        .isConfirmedByGitHub(true)
                        .build(),
                StudentSkill.builder()
                        .id(new StudentSkillId(1L, 11L))
                        .student(student)
                        .skill(css)
                        .level(3)
                        .isConfirmedByGitHub(false)
                        .build()
        ));
        return student;
    }
}
