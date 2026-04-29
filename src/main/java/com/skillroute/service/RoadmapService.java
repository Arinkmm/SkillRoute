package com.skillroute.service;

import com.skillroute.dto.response.*;
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

    @Transactional(readOnly = true)
    public RoadmapResponse generateRoadmap(Long studentId, Long vacancyId) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new EntityNotFoundException("Вакансия не найдена"));

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

        return RoadmapResponse.builder()
                .vacancyId(vacancyId)
                .vacancyName(vacancy.getName())
                .steps(steps)
                .matchPercentage(matchingService.calculateMatch(vacancy.getVacancySkills().size(), steps.size()))
                .build();
    }

    private RoadmapStepResponse buildStep(VacancySkill vs, int currentLevel, Map<Long, List<ResourceResponse>> resourcesMap) {
        Long skillId = vs.getSkill().getId();
        int targetLevel = vs.getLevel();

        return RoadmapStepResponse.builder()
                .skillId(vs.getSkill().getId())
                .skillName(vs.getSkill().getName())
                .currentLevel(currentLevel)
                .targetLevel(targetLevel)
                .gap(matchingService.calculateGapDepth(currentLevel, targetLevel))
                .roadmapStepStatus(matchingService.determineStatus(currentLevel, targetLevel))
                .resources(resourcesMap.getOrDefault(skillId, List.of()))
                .build();
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
                        Collectors.mapping(this::mapToResourceResponse, Collectors.toList())
                ));
    }

    private ResourceResponse mapToResourceResponse(Resource resource) {
        return ResourceResponse.builder()
                .resource(resource.getResource())
                .build();
    }
}