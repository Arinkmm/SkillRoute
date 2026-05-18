package com.skillroute.mapper;

import com.skillroute.dto.response.ResourceResponse;
import com.skillroute.dto.response.RoadmapResponse;
import com.skillroute.dto.response.RoadmapStepResponse;
import com.skillroute.dto.response.RoadmapStepStatus;
import com.skillroute.model.Vacancy;
import com.skillroute.model.VacancySkill;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoadmapMapper {

    public RoadmapResponse toResponse(Vacancy vacancy,
                                      List<RoadmapStepResponse> steps,
                                      double matchPercentage) {
        return RoadmapResponse.builder()
                .vacancyId(vacancy.getId())
                .vacancyName(vacancy.getName())
                .steps(steps)
                .matchPercentage(matchPercentage)
                .build();
    }

    public RoadmapStepResponse toStepResponse(VacancySkill vacancySkill,
                                              int currentLevel,
                                              int gap,
                                              RoadmapStepStatus status,
                                              List<ResourceResponse> resources) {
        return RoadmapStepResponse.builder()
                .skillId(vacancySkill.getSkill().getId())
                .skillName(vacancySkill.getSkill().getName())
                .currentLevel(currentLevel)
                .targetLevel(vacancySkill.getLevel())
                .gap(gap)
                .roadmapStepStatus(status)
                .resources(resources)
                .build();
    }
}
