package com.skillroute.service;

import com.skillroute.dto.request.VacancyFilter;
import com.skillroute.dto.response.VacancyResponse;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.mapper.VacancyMapper;
import com.skillroute.model.StudentProfile;
import com.skillroute.properties.MessageProperties;
import com.skillroute.properties.VacancyCatalogProperties;
import com.skillroute.repository.StudentProfileRepository;
import com.skillroute.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentVacancyCatalogService {
    private final VacancyRepository vacancyRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final VacancyCatalogProperties vacancyCatalogProperties;
    private final MessageProperties messages;
    private final VacancyMapper vacancyMapper;

    @Transactional(readOnly = true)
    public List<VacancyResponse> getFollowedVacancies(Long studentId) {
        return vacancyRepository.findFollowedActiveByStudentId(studentId)
                .stream()
                .map(vacancyMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VacancyResponse> getRecommendedVacanciesForStudent(Long studentId, VacancyFilter filter) {
        StudentProfile profile = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getStudentNotFound()));

        if (profile.getSpecialization() == null) {
            return List.of();
        }

        VacancyFilter effectiveFilter = filter;
        if (effectiveFilter.getSpecializationId() == null) {
            effectiveFilter = VacancyFilter.builder()
                    .minSalary(filter.getMinSalary())
                    .maxSalary(filter.getMaxSalary())
                    .schedule(filter.getSchedule())
                    .specializationId(profile.getSpecialization().getId())
                    .build();
        }

        return vacancyRepository.findFilteredActiveExcludingFollowed(studentId, effectiveFilter)
                .stream()
                .map(vacancyMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VacancyResponse> getFilteredVacanciesForStudent(Long studentId, VacancyFilter filter) {
        return vacancyRepository.findFilteredActiveExcludingFollowed(studentId, filter)
                .stream()
                .map(vacancyMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VacancyResponse> getHighDemandVacanciesForStudent(Long studentId) {
        return vacancyRepository.findHighDemandVacanciesExcludingFollowed(
                        studentId,
                        vacancyCatalogProperties.getHighDemandLimit())
                .stream()
                .map(vacancyMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VacancyResponse> getAllActiveForStudent(Long studentId) {
        return vacancyRepository.findAllActiveExcludingFollowed(studentId)
                .stream()
                .map(vacancyMapper::toResponse)
                .toList();
    }

    public boolean isFilterApplied(VacancyFilter filter) {
        return filter.getMinSalary() != null
                || filter.getMaxSalary() != null
                || filter.getSchedule() != null
                || filter.getSpecializationId() != null;
    }
}
