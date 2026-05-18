package com.skillroute.mapper;

import com.skillroute.dto.response.SkillGapResponse;
import com.skillroute.dto.response.StudentGapResponse;
import com.skillroute.dto.response.StudentPreviewResponse;
import com.skillroute.model.StudentProfile;
import com.skillroute.model.StudentVacancyStatus;
import com.skillroute.model.VacancySkill;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicantMapper {

    public SkillGapResponse toSkillGapResponse(VacancySkill vacancySkill, int currentLevel, int gapDepth) {
        return SkillGapResponse.builder()
                .skillId(vacancySkill.getSkill().getId())
                .skillName(vacancySkill.getSkill().getName())
                .currentLevel(currentLevel)
                .targetLevel(vacancySkill.getLevel())
                .gapDepth(gapDepth)
                .build();
    }

    public StudentGapResponse toStudentGapResponse(StudentProfile student,
                                                   double matchPercentage,
                                                   int totalGapLevel,
                                                   StudentVacancyStatus status,
                                                   List<SkillGapResponse> gaps) {
        return StudentGapResponse.builder()
                .studentId(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .matchPercentage(matchPercentage)
                .totalGapLevel(totalGapLevel)
                .status(status)
                .gaps(gaps)
                .build();
    }

    public StudentPreviewResponse toStudentPreviewResponse(StudentProfile studentProfile, StudentGapResponse gap) {
        return StudentPreviewResponse.builder()
                .studentId(studentProfile.getId())
                .firstName(studentProfile.getFirstName())
                .lastName(studentProfile.getLastName())
                .matchPercentage(gap.getMatchPercentage())
                .totalGapLevel(gap.getTotalGapLevel())
                .status(gap.getStatus())
                .build();
    }
}
