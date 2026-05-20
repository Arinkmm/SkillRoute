package com.skillroute.service;

import com.skillroute.dto.response.*;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.mapper.ResourceMapper;
import com.skillroute.mapper.RoadmapMapper;
import com.skillroute.model.StudentSkill;
import com.skillroute.model.Vacancy;
import com.skillroute.model.VacancySkill;
import com.skillroute.properties.MessageProperties;
import com.skillroute.repository.ResourceRepository;
import com.skillroute.repository.StudentSkillRepository;
import com.skillroute.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoadmapService {
    private final VacancyRepository vacancyRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final ResourceRepository resourceRepository;
    private final MatchingService matchingService;
    private final MessageProperties messages;
    private final RoadmapMapper roadmapMapper;
    private final ResourceMapper resourceMapper;

    @Transactional(readOnly = true)
    public RoadmapResponse generateRoadmap(Long studentId, Long vacancyId) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getVacancyNotFound()));

        Map<Long, Integer> studentSkills = getStudentSkillsMap(studentId);

        List<Long> skillIdsToLearn = vacancy.getVacancySkills().stream()
                .filter(vs -> studentSkills.getOrDefault(vs.getSkill().getId(), 0) < vs.getLevel())
                .map(vs -> vs.getSkill().getId())
                .toList();

        Map<Long, List<ResourceResponse>> resourcesMap = getResourcesForSkills(skillIdsToLearn);

        List<RoadmapStepResponse> steps = vacancy.getVacancySkills().stream()
                .filter(vs -> studentSkills.getOrDefault(vs.getSkill().getId(), 0) < vs.getLevel())
                .map(vs -> {
                    int currentLevel = studentSkills.getOrDefault(vs.getSkill().getId(), 0);
                    return buildStep(vs, currentLevel, resourcesMap);
                })
                .toList();

        return roadmapMapper.toResponse(
                vacancy,
                steps,
                matchingService.calculateMatch(vacancy.getVacancySkills().size(), steps.size()));
    }

    @Transactional(readOnly = true)
    public RoadmapStepResponse getRoadmapStep(Long studentId, Long vacancyId, Long skillId) {
        return generateRoadmap(studentId, vacancyId).getSteps().stream()
                .filter(step -> step.getSkillId().equals(skillId))
                .findFirst()
                .orElse(null);
    }

    private RoadmapStepResponse buildStep(VacancySkill vs, int currentLevel, Map<Long, List<ResourceResponse>> resourcesMap) {
        Long skillId = vs.getSkill().getId();
        int targetLevel = vs.getLevel();

        return roadmapMapper.toStepResponse(
                vs,
                currentLevel,
                matchingService.calculateGapDepth(currentLevel, targetLevel),
                matchingService.determineStatus(currentLevel, targetLevel),
                resourcesMap.getOrDefault(skillId, List.of()));
    }

    private Map<Long, Integer> getStudentSkillsMap(Long studentId) {
        return studentSkillRepository.findAllByStudentId(studentId).stream()
                .collect(Collectors.toMap(
                        ss -> ss.getSkill().getId(),
                        StudentSkill::getLevel
                ));
    }

    private Map<Long, List<ResourceResponse>> getResourcesForSkills(List<Long> skillIds) {
        if (skillIds.isEmpty()) return Map.of();

        return resourceRepository.findAllBySkillIdIn(skillIds).stream()
                .collect(Collectors.groupingBy(
                        r -> r.getSkill().getId(),
                        Collectors.mapping(resourceMapper::toResponse, Collectors.toList())
                ));
    }
}
