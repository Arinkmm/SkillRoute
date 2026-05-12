package com.skillroute.dto.response;

import lombok.Builder;
import lombok.Data;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

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
}
