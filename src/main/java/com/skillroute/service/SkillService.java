package com.skillroute.service;

import com.skillroute.dto.request.AddResourceRequest;
import com.skillroute.dto.response.RouteSkillResponse;
import com.skillroute.dto.response.SkillResponse;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.model.Skill;
import com.skillroute.properties.MessageProperties;
import com.skillroute.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final MessageProperties messages;

    @Transactional(readOnly = true)
    public List<SkillResponse> getSkills() {
        return skillRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public SkillResponse getSkillById(Long id) {
        return skillRepository.findById(id)
                .map(this::mapToResponseDto)
                .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getSkillNotFound()));
    }

    public RouteSkillResponse getRouteSkillById(Long id) {
        return skillRepository.findById(id)
                .map(this::mapToRouteSkillDto)
                .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getSkillNotFound()));
    }

    private RouteSkillResponse mapToRouteSkillDto(Skill skill) {
        return RouteSkillResponse.builder()
                .skillId(skill.getId())
                .name(skill.getName())
                .resources(skill.getResources().stream()
                        .map(res -> AddResourceRequest.builder()
                                .resource(res.getResource())
                                .build())
                        .toList())
                .build();
    }

    private SkillResponse mapToResponseDto(Skill skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .name(skill.getName())
                .build();
    }
}
