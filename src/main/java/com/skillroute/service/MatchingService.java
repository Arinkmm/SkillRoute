package com.skillroute.service;

import com.skillroute.dto.response.RoadmapStepStatus;
import org.springframework.stereotype.Service;

@Service
public class MatchingService {
    public double calculateMatch(int totalRequiredSkills, int skillsWithGap) {
        if (totalRequiredSkills == 0) return 100.0;
        double match = (double) (totalRequiredSkills - skillsWithGap) / totalRequiredSkills * 100;
        return Math.round(match * 10.0) / 10.0;
    }

    public int calculateGapDepth(int currentLevel, int targetLevel) {
        return Math.max(0, targetLevel - currentLevel);
    }

    public RoadmapStepStatus determineStatus(int currentLevel, int targetLevel) {
        if (currentLevel >= targetLevel) {
            return null;
        }
        return currentLevel == 0 ? RoadmapStepStatus.MISSING : RoadmapStepStatus.UPGRADE_REQUIRED;
    }
}
