package com.skillroute.mapper;

import com.skillroute.dto.response.VacancyResponse;
import com.skillroute.model.CompanyProfile;
import com.skillroute.model.Direction;
import com.skillroute.model.Language;
import com.skillroute.model.Skill;
import com.skillroute.model.Specialization;
import com.skillroute.model.Vacancy;
import com.skillroute.model.VacancyProfile;
import com.skillroute.model.VacancySkill;
import com.skillroute.model.VacancyStatus;
import com.skillroute.model.WorkSchedule;
import com.skillroute.model.id.VacancySkillId;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VacancyMapperTest {
    private final VacancyMapper mapper = new VacancyMapper(new SpecializationMapper());

    @Test
    void toResponseMapsCompanyNameProfileSpecializationAndSkills() {
        CompanyProfile company = CompanyProfile.builder()
                .id(2L)
                .companyName("SkillRoute Labs")
                .build();
        Specialization specialization = Specialization.builder()
                .id(3L)
                .direction(Direction.BACKEND)
                .language(Language.JAVA)
                .build();
        Vacancy vacancy = Vacancy.builder()
                .id(10L)
                .name("Java Developer")
                .company(company)
                .build();
        vacancy.setProfile(VacancyProfile.builder()
                .vacancy(vacancy)
                .specialization(specialization)
                .salary(150000L)
                .workSchedule(WorkSchedule.REMOTE)
                .status(VacancyStatus.IN_PROGRESS)
                .build());
        Skill skill = Skill.builder().id(20L).name("Spring Boot").build();
        vacancy.setVacancySkills(Set.of(VacancySkill.builder()
                .id(new VacancySkillId(10L, 20L))
                .vacancy(vacancy)
                .skill(skill)
                .level(4)
                .build()));

        VacancyResponse response = mapper.toResponse(vacancy);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getCompanyId()).isEqualTo(2L);
        assertThat(response.getCompanyName()).isEqualTo("SkillRoute Labs");
        assertThat(response.getSalary()).isEqualTo(150000L);
        assertThat(response.getWorkSchedule()).isEqualTo(WorkSchedule.REMOTE);
        assertThat(response.getStatus()).isEqualTo(VacancyStatus.IN_PROGRESS);
        assertThat(response.getSpecialization().getDirection()).isEqualTo(Direction.BACKEND);
        assertThat(response.getSkills()).singleElement()
                .satisfies(mappedSkill -> {
                    assertThat(mappedSkill.getSkillId()).isEqualTo(20L);
                    assertThat(mappedSkill.getName()).isEqualTo("Spring Boot");
                    assertThat(mappedSkill.getLevel()).isEqualTo(4);
                });
    }
}
