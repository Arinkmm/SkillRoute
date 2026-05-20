package com.skillroute.dto.response;

import com.skillroute.model.StudentVacancyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyStudentResponse {
    private Long studentId;
    private String firstName;
    private String lastName;
    private String specializationName;
    private Long vacancyId;
    private String vacancyName;
    private StudentVacancyStatus status;
    private int skillCount;
    private int confirmedSkillCount;
}
