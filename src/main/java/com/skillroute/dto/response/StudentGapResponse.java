package com.skillroute.dto.response;

import com.skillroute.model.StudentVacancyStatus;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentGapResponse {
    private Long studentId;
    private String firstName;
    private String lastName;
    private double matchPercentage;
    private int totalGapLevel;
    private List<SkillGapResponse> gaps;
    private StudentVacancyStatus status;
}
