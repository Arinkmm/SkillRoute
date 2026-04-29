package com.skillroute.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentSkillResponse {
    private String name;
    private int level;
    private boolean isConfirmedByGitHub;
}
