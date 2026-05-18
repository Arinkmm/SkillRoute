package com.skillroute.mapper;

import com.skillroute.dto.response.VacancyResponse;
import com.skillroute.dto.response.VacancySkillResponse;
import com.skillroute.model.Vacancy;
import com.skillroute.model.VacancyProfile;
import com.skillroute.model.VacancySkill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VacancyMapper {
    private final SpecializationMapper specializationMapper;

    public VacancyResponse toResponse(Vacancy vacancy) {
        VacancyProfile profile = vacancy.getProfile();

        return VacancyResponse.builder()
                .id(vacancy.getId())
                .name(vacancy.getName())
                .companyId(vacancy.getCompany().getId())
                .salary(profile.getSalary())
                .workSchedule(profile.getWorkSchedule())
                .status(profile.getStatus())
                .specialization(specializationMapper.toResponse(profile.getSpecialization()))
                .skills(vacancy.getVacancySkills().stream()
                        .map(this::toSkillResponse)
                        .toList())
                .build();
    }

    private VacancySkillResponse toSkillResponse(VacancySkill vacancySkill) {
        return new VacancySkillResponse(
                vacancySkill.getSkill().getId(),
                vacancySkill.getSkill().getName(),
                vacancySkill.getLevel());
    }
}
