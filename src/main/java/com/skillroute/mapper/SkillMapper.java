package com.skillroute.mapper;

import com.skillroute.dto.response.RouteSkillResponse;
import com.skillroute.dto.response.SkillResponse;
import com.skillroute.model.Resource;
import com.skillroute.model.Skill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
@RequiredArgsConstructor
public class SkillMapper {
    private final ResourceMapper resourceMapper;

    public SkillResponse toResponse(Skill skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .name(skill.getName())
                .resources(skill.getResources().stream()
                        .sorted(Comparator.comparing(Resource::getId))
                        .map(resourceMapper::toResponse)
                        .toList())
                .build();
    }

    public RouteSkillResponse toRouteResponse(Skill skill) {
        return RouteSkillResponse.builder()
                .skillId(skill.getId())
                .name(skill.getName())
                .resources(skill.getResources().stream()
                        .sorted(Comparator.comparing(Resource::getId))
                        .map(resourceMapper::toResponse)
                        .toList())
                .build();
    }
}
