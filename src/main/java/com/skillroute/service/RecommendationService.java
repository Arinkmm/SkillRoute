package com.skillroute.service;

import com.skillroute.dto.VacancySkillResponse;
import com.skillroute.dto.VacancyResponse;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.model.*;
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

    @Transactional(readOnly = true)
    public List<VacancyResponse> getRecommendedVacanciesForStudent(Long studentId) {
        StudentProfile profile = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Студент не найден"));

        if (profile.getSpecialization() == null) return List.of();

        return vacancyRepository.findAllByProfileSpecializationIdAndProfileStatus(
                profile.getSpecialization().getId(), VacancyStatus.OPEN)
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
                .language(spec.getLanguage())
                .direction(spec.getDirection())
                .fullSpecialization(spec.getLanguage() + " (" + spec.getDirection() + ")")
                .skills(vacancy.getVacancySkills().stream()
                        .map(vs -> new VacancySkillResponse(
                                vs.getSkill().getId(),
                                vs.getSkill().getName(),
                                vs.getLevel()))
                        .collect(Collectors.toList()))
                .build();
    }
}