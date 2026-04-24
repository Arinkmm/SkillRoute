package com.skillroute.dto;

import com.skillroute.model.VacancySkill;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkillGapReport {
    private List<VacancySkill> missingSkills;
    private List<SkillLevelDifference> lowLevelSkills;
}
