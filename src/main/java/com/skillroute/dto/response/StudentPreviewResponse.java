package com.skillroute.dto.response;

import com.skillroute.model.StudentVacancyStatus;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentPreviewResponse {
    private Long studentId;
    private String firstName;
    private String lastName;
    private double matchPercentage;
    private int totalGapLevel;
    private StudentVacancyStatus status;
}
