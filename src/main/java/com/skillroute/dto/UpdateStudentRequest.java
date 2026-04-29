package com.skillroute.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStudentRequest {
    private String firstName;
    private String lastName;
    private String gitHubUrl;
    private Long specializationId;
    private String bio;

}
