package com.skillroute.service;

import com.skillroute.dto.*;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.model.Resource;
import com.skillroute.model.StudentSkill;
import com.skillroute.model.Vacancy;
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

    @Transactional(readOnly = true)
    public RoadmapResponse generateRoadmap(Long studentId, Long vacancyId) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new EntityNotFoundException("Вакансия не найдена"));

        Map<Long, Integer> studentSkills = studentSkillRepository.findAllByStudentId(studentId)
                .stream()
                .collect(Collectors.toMap(
                        ss -> ss.getSkill().getId(),
                        StudentSkill::getLevel
                ));

        List<Long> skillIds = vacancy.getVacancySkills().stream()
                .map(vs -> vs.getSkill().getId())
                .toList();

        Map<Long, List<ResourceResponse>> resourcesMap = resourceRepository.findAllBySkillIdIn(skillIds)
                .stream()
                .collect(Collectors.groupingBy(
                        r -> r.getSkill().getId(),
                        Collectors.mapping(this::mapToResourceResponse, Collectors.toList())
                ));

        List<RoadmapStepResponse> steps = vacancy.getVacancySkills().stream()
                .filter(vs -> studentSkills.getOrDefault(vs.getSkill().getId(), 0) < vs.getLevel())
                .map(vs -> {
                    Long sId = vs.getSkill().getId();
                    int current = studentSkills.getOrDefault(sId, 0);

                    return RoadmapStepResponse.builder()
                            .skillName(vs.getSkill().getName())
                            .currentLevel(current)
                            .targetLevel(vs.getLevel())
                            .resources(resourcesMap.getOrDefault(sId, List.of()))
                            .roadmapStepStatus(current == 0 ? RoadmapStepStatus.MISSING : RoadmapStepStatus.UPGRADE_REQUIRED)
                            .build();
                }).toList();

        return RoadmapResponse.builder()
                .vacancyId(vacancyId)
                .steps(steps)
                .matchPercentage(calculateMatch(vacancy.getVacancySkills().size(), steps.size()))
                .build();
    }

    private ResourceResponse mapToResourceResponse(Resource resource) {
        return ResourceResponse.builder()
                .resource(resource.getResource())
                .build();
    }

    private double calculateMatch(int total, int gaps) {
        if (total == 0) return 100.0;
        double match = (double) (total - gaps) / total * 100;
        return Math.round(match * 10.0) / 10.0;
    }
}