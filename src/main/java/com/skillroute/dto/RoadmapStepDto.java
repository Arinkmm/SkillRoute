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
public class RoadmapStepDto {
    private String skillName;
    private int currentLevel;
    private int targetLevel;
    private List<String> resources;
    private Status status;
}