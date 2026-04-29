package com.skillroute.service;

import com.skillroute.dto.AddResourceRequest;
import com.skillroute.dto.RouteSkillResponse;
import com.skillroute.dto.SkillResponse;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.model.Skill;
import com.skillroute.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;

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
                .orElseThrow(() -> new EntityNotFoundException("Скилл не найден"));
    }

    public RouteSkillResponse getRouteSkillById(Long id) {
        return skillRepository.findById(id)
                .map(this::mapToRouteSkillDto)
                .orElseThrow(() -> new EntityNotFoundException("Скилл не найден"));
    }

    private RouteSkillResponse mapToRouteSkillDto(Skill skill) {
        return RouteSkillResponse.builder()
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
