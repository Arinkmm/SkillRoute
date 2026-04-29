package com.skillroute.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentProfileResponse {
    private String firstName;
    private String lastName;
    private String githubUrl;
    private Long specializationId;
    private String bio;
}
