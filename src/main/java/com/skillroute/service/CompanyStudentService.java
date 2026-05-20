package com.skillroute.service;

import com.skillroute.dto.response.CompanyStudentDetailsResponse;
import com.skillroute.dto.response.CompanyStudentResponse;
import com.skillroute.dto.response.StudentSkillResponse;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.mapper.CompanyStudentMapper;
import com.skillroute.model.StudentProfile;
import com.skillroute.model.StudentVacancy;
import com.skillroute.model.StudentVacancyStatus;
import com.skillroute.properties.MessageProperties;
import com.skillroute.repository.StudentProfileRepository;
import com.skillroute.repository.StudentVacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyStudentService {
    private final StudentProfileRepository studentProfileRepository;
    private final StudentVacancyRepository studentVacancyRepository;
    private final CompanyStudentMapper companyStudentMapper;
    private final MessageProperties messages;

    @Transactional(readOnly = true)
    public List<CompanyStudentResponse> getTrackedStudents(Long companyId) {
        return studentVacancyRepository.findAllByCompanyIdAndStatusIn(companyId, List.of(
                StudentVacancyStatus.REVIEWING,
                StudentVacancyStatus.INTERVIEW
        ))
                .stream()
                .collect(Collectors.toMap(
                        application -> application.getStudent().getId(),
                        Function.identity(),
                        this::choosePreferredApplication,
                        LinkedHashMap::new))
                .values()
                .stream()
                .map(companyStudentMapper::toTrackedResponse)
                .sorted(Comparator.comparing(CompanyStudentResponse::getFirstName, Comparator.nullsLast(String::compareToIgnoreCase)))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CompanyStudentResponse> getAvailableStudents(Long companyId) {
        Set<Long> trackedIds = getTrackedStudents(companyId).stream()
                .map(CompanyStudentResponse::getStudentId)
                .collect(Collectors.toSet());

        return studentProfileRepository.findAllByFirstNameIsNotNullAndLastNameIsNotNullOrderByFirstNameAscLastNameAsc()
                .stream()
                .filter(student -> !trackedIds.contains(student.getId()))
                .map(companyStudentMapper::toCatalogResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CompanyStudentDetailsResponse getStudentDetails(Long studentId) {
        StudentProfile student = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getStudentNotFound()));

        List<StudentSkillResponse> skills = student.getStudentSkills().stream()
                .map(companyStudentMapper::toSkillResponse)
                .sorted(Comparator.comparing(StudentSkillResponse::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        return companyStudentMapper.toDetailsResponse(student, skills);
    }

    private StudentVacancy choosePreferredApplication(StudentVacancy current, StudentVacancy candidate) {
        int statusComparison = Integer.compare(
                getStatusPriority(current.getStatus()),
                getStatusPriority(candidate.getStatus()));

        if (statusComparison != 0) {
            return statusComparison > 0 ? current : candidate;
        }

        return current.getVacancy().getId() > candidate.getVacancy().getId() ? current : candidate;
    }

    private int getStatusPriority(StudentVacancyStatus status) {
        return switch (status) {
            case INTERVIEW -> 2;
            case REVIEWING -> 1;
            default -> 0;
        };
    }
}
