package com.skillroute.service;

import com.skillroute.TestMessageProperties;
import com.skillroute.dto.response.RoadmapResponse;
import com.skillroute.dto.response.RoadmapStepResponse;
import com.skillroute.dto.response.RoadmapStepStatus;
import com.skillroute.mapper.ResourceMapper;
import com.skillroute.mapper.RoadmapMapper;
import com.skillroute.model.Resource;
import com.skillroute.model.Skill;
import com.skillroute.model.StudentSkill;
import com.skillroute.model.Vacancy;
import com.skillroute.model.VacancySkill;
import com.skillroute.model.id.StudentSkillId;
import com.skillroute.model.id.VacancySkillId;
import com.skillroute.repository.ResourceRepository;
import com.skillroute.repository.StudentSkillRepository;
import com.skillroute.repository.VacancyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoadmapServiceTest {
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private StudentSkillRepository studentSkillRepository;
    @Mock
    private ResourceRepository resourceRepository;

    private RoadmapService service;

    @BeforeEach
    void setUp() {
        service = new RoadmapService(
                vacancyRepository,
                studentSkillRepository,
                resourceRepository,
                new MatchingService(),
                TestMessageProperties.create(),
                new RoadmapMapper(),
                new ResourceMapper());
    }

    @Test
    void generateRoadmapReturnsOnlyMissingAndUpgradeStepsWithResources() {
        Skill java = Skill.builder().id(10L).name("Java").build();
        Skill spring = Skill.builder().id(11L).name("Spring").build();
        Skill sql = Skill.builder().id(12L).name("SQL").build();
        Vacancy vacancy = Vacancy.builder().id(100L).name("Backend").build();
        VacancySkill javaRequirement = vacancySkill(vacancy, java, 3);
        VacancySkill springRequirement = vacancySkill(vacancy, spring, 4);
        VacancySkill sqlRequirement = vacancySkill(vacancy, sql, 2);
        vacancy.setVacancySkills(Set.of(javaRequirement, springRequirement, sqlRequirement));

        when(vacancyRepository.findById(100L)).thenReturn(Optional.of(vacancy));
        when(studentSkillRepository.findAllByStudentId(1L)).thenReturn(List.of(
                studentSkill(1L, java, 3),
                studentSkill(1L, spring, 2)
        ));
        when(resourceRepository.findAllBySkillIdIn(anyList())).thenReturn(List.of(
                Resource.builder().id(50L).skill(spring).resource("https://spring.io").build(),
                Resource.builder().id(51L).skill(sql).resource("https://sql.example").build()
        ));

        RoadmapResponse roadmap = service.generateRoadmap(1L, 100L);

        assertThat(roadmap.getVacancyName()).isEqualTo("Backend");
        assertThat(roadmap.getMatchPercentage()).isEqualTo(33.3);
        assertThat(roadmap.getSteps())
                .extracting(RoadmapStepResponse::getSkillName)
                .containsExactlyInAnyOrder("Spring", "SQL");

        RoadmapStepResponse springStep = roadmap.getSteps().stream()
                .filter(step -> step.getSkillName().equals("Spring"))
                .findFirst()
                .orElseThrow();
        assertThat(springStep.getCurrentLevel()).isEqualTo(2);
        assertThat(springStep.getTargetLevel()).isEqualTo(4);
        assertThat(springStep.getGap()).isEqualTo(2);
        assertThat(springStep.getRoadmapStepStatus()).isEqualTo(RoadmapStepStatus.UPGRADE_REQUIRED);
        assertThat(springStep.getResources()).hasSize(1);

        RoadmapStepResponse sqlStep = roadmap.getSteps().stream()
                .filter(step -> step.getSkillName().equals("SQL"))
                .findFirst()
                .orElseThrow();
        assertThat(sqlStep.getCurrentLevel()).isZero();
        assertThat(sqlStep.getRoadmapStepStatus()).isEqualTo(RoadmapStepStatus.MISSING);
    }

    @Test
    void getRoadmapStepReturnsNullWhenSkillIsNotGap() {
        Skill java = Skill.builder().id(10L).name("Java").build();
        Vacancy vacancy = Vacancy.builder().id(100L).name("Backend").build();
        vacancy.setVacancySkills(Set.of(vacancySkill(vacancy, java, 3)));

        when(vacancyRepository.findById(100L)).thenReturn(Optional.of(vacancy));
        when(studentSkillRepository.findAllByStudentId(1L)).thenReturn(List.of(studentSkill(1L, java, 3)));

        assertThat(service.getRoadmapStep(1L, 100L, 10L)).isNull();
    }

    private VacancySkill vacancySkill(Vacancy vacancy, Skill skill, int level) {
        return VacancySkill.builder()
                .id(new VacancySkillId(vacancy.getId(), skill.getId()))
                .vacancy(vacancy)
                .skill(skill)
                .level(level)
                .build();
    }

    private StudentSkill studentSkill(Long studentId, Skill skill, int level) {
        return StudentSkill.builder()
                .id(new StudentSkillId(studentId, skill.getId()))
                .skill(skill)
                .level(level)
                .build();
    }
}
