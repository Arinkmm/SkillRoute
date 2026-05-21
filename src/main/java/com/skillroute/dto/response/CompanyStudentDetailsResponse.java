package com.skillroute.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyStudentDetailsResponse {
    private Long studentId;
    private String firstName;
    private String lastName;
    private SpecializationResponse specialization;
    private String githubUrl;
    private String bio;
    private List<StudentSkillResponse> skills;
}
