package com.skillroute.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoadmapResponse {
    private Long vacancyId;
    private String vacancyName;
    private List<RoadmapStepResponse> steps;
    private double matchPercentage;
 }