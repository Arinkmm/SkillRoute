package com.skillroute.service;

import com.skillroute.dto.response.RoadmapStepStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MatchingServiceTest {
    private final MatchingService matchingService = new MatchingService();

    @Test
    void calculateMatchReturnsPerfectScoreWhenVacancyHasNoRequiredSkills() {
        assertThat(matchingService.calculateMatch(0, 0)).isEqualTo(100.0);
    }

    @Test
    void calculateMatchRoundsToOneDecimal() {
        assertThat(matchingService.calculateMatch(3, 1)).isEqualTo(66.7);
    }

    @Test
    void determineStatusDistinguishesMissingAndUpgradeRequired() {
        assertThat(matchingService.determineStatus(0, 3)).isEqualTo(RoadmapStepStatus.MISSING);
        assertThat(matchingService.determineStatus(2, 3)).isEqualTo(RoadmapStepStatus.UPGRADE_REQUIRED);
        assertThat(matchingService.determineStatus(3, 3)).isNull();
    }
}
