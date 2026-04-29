package com.skillroute.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkillGapResponse {
    private Long skillId;
    private String skillName;
    private int currentLevel;
    private int targetLevel;
    private int gapDepth;
}
