package com.skillroute.service;

import com.skillroute.dto.SkillVacancyResponse;
import com.skillroute.dto.VacancyResponse;
import com.skillroute.exception.DuplicateEntityException;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.model.*;
import com.skillroute.model.id.StudentVacancyId;
import com.skillroute.repository.StudentProfileRepository;
import com.skillroute.repository.StudentVacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentVacancyService {
    private final StudentVacancyRepository studentVacancyRepository;
    private final StudentProfileRepository studentProfileRepository;

    @Transactional(readOnly = true)
    public List<VacancyResponse> getFollowedVacancies(Long studentId) {
        StudentProfile profile = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Студент не найден"));

        return profile.getStudentVacancies().stream()
                .map(StudentVacancy::getVacancy)
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void applyToVacancy(Long studentId, Long vacancyId) {
        StudentVacancyId id = new StudentVacancyId(studentId, vacancyId);

        if (studentVacancyRepository.existsById(id)) {
            throw new DuplicateEntityException("Студент уже отслеживает эту вакансию");
        }

        StudentVacancy application = StudentVacancy.builder()
                .id(id)
                .status(StudentVacancyStatus.SUBMITTED)
                .build();

        studentVacancyRepository.save(application);
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
                        .map(vs -> new SkillVacancyResponse(
                                vs.getSkill().getId(),
                                vs.getSkill().getName(),
                                vs.getLevel()))
                        .collect(Collectors.toList()))
                .build();
    }
}
