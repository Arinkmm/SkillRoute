package com.skillroute.service;

import com.skillroute.dto.request.ApplicantFilter;
import com.skillroute.dto.response.SkillGapResponse;
import com.skillroute.dto.response.StudentGapResponse;
import com.skillroute.dto.response.StudentPreviewResponse;
import com.skillroute.model.*;
import com.skillroute.repository.StudentProfileRepository;
import com.skillroute.repository.StudentVacancyRepository;
import com.skillroute.repository.VacancyProfileRepository;
import com.skillroute.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicantService {
    private final StudentProfileRepository studentProfileRepository;
    private final StudentVacancyRepository studentVacancyRepository;
    private final VacancyProfileRepository vacancyProfileRepository;
    private final MatchingService matchingService;
    private final VacancyRepository vacancyRepository;

    @Transactional(readOnly = true)
    public List<StudentPreviewResponse> getFilteredApplicants(Long vacancyId, ApplicantFilter filter) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId).orElseThrow();

        return studentProfileRepository.findAll().stream()
                .map(s -> buildPreview(s, vacancy))
                .filter(s -> s.getMatchPercentage() >= filter.getMinMatch())
                .filter(s -> filter.getMaxGap() == 0 || s.getTotalGapLevel() <= filter.getMaxGap())
                .sorted(Comparator.comparingDouble(StudentPreviewResponse::getMatchPercentage).reversed())
                .toList();
    }

    @Transactional(readOnly = true)
    public StudentGapResponse getStudentGap(Long studentId, Long vacancyId) {
        StudentProfile student = studentProfileRepository.findById(studentId).orElseThrow();
        Vacancy vacancy = vacancyRepository.findById(vacancyId).orElseThrow();

        Map<Long, Integer> studentSkills = student.getStudentSkills().stream()
                .collect(Collectors.toMap(ss -> ss.getSkill().getId(), StudentSkill::getLevel));

        List<SkillGapResponse> gaps = vacancy.getVacancySkills().stream()
                .filter(vs -> studentSkills.getOrDefault(vs.getSkill().getId(), 0) < vs.getLevel())
                .map(vs -> {
                    int current = studentSkills.getOrDefault(vs.getSkill().getId(), 0);
                    return SkillGapResponse.builder()
                            .skillId(vs.getSkill().getId())
                            .skillName(vs.getSkill().getName())
                            .currentLevel(current)
                            .targetLevel(vs.getLevel())
                            .gapDepth(matchingService.calculateGapDepth(current, vs.getLevel()))
                            .build();
                }).toList();

        return StudentGapResponse.builder()
                .studentId(studentId)
                .matchPercentage(matchingService.calculateMatch(vacancy.getVacancySkills().size(), gaps.size()))
                .totalGapLevel(gaps.stream().mapToInt(SkillGapResponse::getGapDepth).sum())
                .gaps(gaps).build();
    }

    @Transactional
    public void startReviewing(Long studentId, Long vacancyId) {
        studentVacancyRepository.findByStudentIdAndVacancyId(studentId, vacancyId)
                .ifPresent(rel -> rel.setStatus(StudentVacancyStatus.REVIEWING));
        vacancyProfileRepository.findById(vacancyId)
                .ifPresent(profile -> profile.setStatus(VacancyStatus.IN_PROGRESS));
    }

    @Transactional
    public void startInterviewing(Long studentId, Long vacancyId) {
        studentVacancyRepository.findByStudentIdAndVacancyId(studentId, vacancyId)
                .ifPresent(rel -> rel.setStatus(StudentVacancyStatus.INTERVIEW));
    }

    @Transactional
    public void rejectStudent(Long studentId, Long vacancyId) {
        studentVacancyRepository.findByStudentIdAndVacancyId(studentId, vacancyId)
                .ifPresent(rel -> rel.setStatus(StudentVacancyStatus.REJECTED));
    }

    @Transactional
    public void acceptStudent(Long studentId, Long vacancyId) {
        studentVacancyRepository.findByStudentIdAndVacancyId(studentId, vacancyId)
                .ifPresent(rel -> rel.setStatus(StudentVacancyStatus.ACCEPTED));
        vacancyProfileRepository.findById(vacancyId)
                .ifPresent(profile -> profile.setStatus(VacancyStatus.CLOSE));
    }

    private StudentPreviewResponse buildPreview(StudentProfile studentProfile, Vacancy vacancy) {
        StudentGapResponse gap = getStudentGap(studentProfile.getId(), vacancy.getId());
        return StudentPreviewResponse.builder()
                .studentId(studentProfile.getId())
                .firstName(studentProfile.getFirstName())
                .lastName(studentProfile.getLastName())
                .matchPercentage(gap.getMatchPercentage())
                .totalGapLevel(gap.getTotalGapLevel())
                .build();
    }
}