package com.skillroute.service;

import com.skillroute.dto.request.AddResourceRequest;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.model.Resource;
import com.skillroute.model.Skill;
import com.skillroute.repository.ResourceRepository;
import com.skillroute.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final SkillRepository skillRepository;
    private final ResourceRepository resourceRepository;

    @Transactional
    public void addResourceToSkill(Long skillId, AddResourceRequest form) {
        Skill skill = skillRepository.findById(skillId).orElseThrow(() -> new EntityNotFoundException("Скилл не найден"));

        Resource resource = Resource.builder()
                .resource(form.getResource())
                .skill(skill)
                .build();

        resourceRepository.save(resource);
    }
}
