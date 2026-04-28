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

        Map<Long, Integer> studentSkills = studentSkillRepository.findAllByStudentId(studentId)
                .stream()
                .collect(Collectors.toMap(ss -> ss.getSkill().getId(), StudentSkill::getLevel));

        List<Long> skillIds = vacancy.getVacancySkills().stream()
                .map(vs -> vs.getSkill().getId()).toList();

        Map<Long, List<String>> resourcesMap = resourceRepository.findAllBySkillIdIn(skillIds)
                .stream()
                .collect(Collectors.groupingBy(
                        r -> r.getSkill().getId(),
                        Collectors.mapping(Resource::getResource, Collectors.toList())
                ));

        List<RoadmapStepDto> steps = vacancy.getVacancySkills().stream()
                .filter(vs -> studentSkills.getOrDefault(vs.getSkill().getId(), 0) < vs.getLevel())
                .map(vs -> {
                    Long sId = vs.getSkill().getId();
                    int current = studentSkills.getOrDefault(sId, 0);
                    return new RoadmapStepDto(
                            vs.getSkill().getName(),
                            current,
                            vs.getLevel(),
                            resourcesMap.getOrDefault(sId, List.of()),
                            current == 0 ? Status.MISSING : Status.UPGRADE_REQUIRED
                    );
                }).toList();

        return RoadmapResponseDto.builder()
                .vacancyId(vacancyId)
                .steps(steps)
                .matchPercentage(calculateMatch(vacancy.getVacancySkills().size(), steps.size()))
                .build();
    }

    private double calculateMatch(int total, int gaps) {
        return total == 0 ? 100.0 : Math.max(0, 100.0 * (total - gaps) / total);
    }
}