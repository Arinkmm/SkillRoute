package com.skillroute.service;

import com.skillroute.dto.*;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.model.Resource;
import com.skillroute.model.StudentSkill;
import com.skillroute.model.Vacancy;
import com.skillroute.model.VacancySkill;
import com.skillroute.repository.ResourceRepository;
import com.skillroute.repository.StudentSkillRepository;
import com.skillroute.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoadmapService {
    private final VacancyRepository vacancyRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final ResourceRepository resourceRepository;

    @Transactional(readOnly = true)
    public RoadmapResponseDto generateRoadmap(Long studentId, Long vacancyId) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new EntityNotFoundException("Вакансия не найдена: " + vacancyId));

        Map<Long, Integer> studentSkillsMap = studentSkillRepository.findAllByStudentId(studentId)
                .stream()
                .collect(Collectors.toMap(sk -> sk.getId().getSkillId(), StudentSkill::getLevel));

        List<RoadmapStepDto> steps = new ArrayList<>();

        for (VacancySkill req : vacancy.getVacancySkills()) {
            Long sId = req.getSkill().getId();
            int required = req.getLevel();
            int current = studentSkillsMap.getOrDefault(sId, 0);

            if (current < required) {
                Status status = (current == 0) ? Status.MISSING : Status.UPGRADE_REQUIRED;
                steps.add(mapToStep(sId, req.getSkill().getName(), current, required, status));
            }
        }

        return RoadmapResponseDto.builder()
                .vacancyId(vacancyId)
                .vacancyName(vacancy.getName())
                .steps(steps)
                .matchPercentage(calculateMatch(vacancy.getVacancySkills().size(), steps.size()))
                .build();
    }

    private RoadmapStepDto mapToStep(Long skillId, String name, int cur, int target, Status status) {
        List<String> resources = resourceRepository.findAllBySkillId(skillId)
                .stream().map(Resource::getResource).toList();
        return new RoadmapStepDto(name, cur, target, resources, status);
    }

    private double calculateMatch(int total, int gaps) {
        return total == 0 ? 100.0 : Math.max(0, 100.0 * (total - gaps) / total);
    }
}