package com.skillroute.service;

import com.skillroute.dto.response.RouteSkillResponse;
import com.skillroute.dto.response.SkillResponse;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.mapper.SkillMapper;
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
    private final SkillMapper skillMapper;

    @Transactional(readOnly = true)
    public List<SkillResponse> getSkills() {
        return skillRepository.findAll().stream()
                .map(skillMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SkillResponse getSkillById(Long id) {
        return skillRepository.findById(id)
                .map(skillMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getSkillNotFound()));
    }

    @Transactional(readOnly = true)
    public RouteSkillResponse getRouteSkillById(Long id) {
        return skillRepository.findById(id)
                .map(skillMapper::toRouteResponse)
                .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getSkillNotFound()));
    }
}
