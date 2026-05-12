package com.skillroute.service;

import com.skillroute.dto.request.VacancyFilter;
import com.skillroute.dto.response.SpecializationResponse;
import com.skillroute.dto.response.VacancySkillResponse;
import com.skillroute.dto.response.VacancyResponse;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.model.*;
import com.skillroute.properties.MessageProperties;
import com.skillroute.repository.StudentProfileRepository;
import com.skillroute.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final VacancyRepository vacancyRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final MessageProperties messages;

    @Transactional(readOnly = true)
    public List<VacancyResponse> getRecommendedVacanciesForStudent(Long studentId, VacancyFilter filter) {
        StudentProfile profile = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getStudentNotFound()));

        if (profile.getSpecialization() == null) return List.of();

        return vacancyRepository.findRecommendedWithSubquery(studentId, filter)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    private VacancyResponse mapToResponseDto(Vacancy vacancy) {
        VacancyProfile profile = vacancy.getProfile();
        Specialization spec = profile.getSpecialization();

        return VacancyResponse.builder()
                .id(vacancy.getId())
                .name(vacancy.getName())
                .companyId(vacancy.getCompany().getId())
                .salary(profile.getSalary())
                .workSchedule(profile.getWorkSchedule())
                .status(profile.getStatus())
                .specialization(mapSpecialization(spec))
                .skills(vacancy.getVacancySkills().stream()
                        .map(vs -> new VacancySkillResponse(
                                vs.getSkill().getId(),
                                vs.getSkill().getName(),
                                vs.getLevel()))
                        .collect(Collectors.toList()))
                .build();
    }

    private SpecializationResponse mapSpecialization(Specialization specialization) {
        if (specialization == null) {
            return null;
        }

        return SpecializationResponse.builder()
                .id(specialization.getId())
                .direction(specialization.getDirection())
                .language(specialization.getLanguage())
                .build();
    }
}
