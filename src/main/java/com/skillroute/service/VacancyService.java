package com.skillroute.service;

import com.skillroute.dto.*;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.exception.ResourceOwnershipException;
import com.skillroute.model.*;
import com.skillroute.model.id.VacancySkillId;
import com.skillroute.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final SpecializationRepository specializationRepository;

    @Transactional(readOnly = true)
    public List<VacancyResponseDto> getAllActive() {
        return vacancyRepository.findAllByProfileStatus(VacancyStatus.OPEN)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VacancyResponseDto> getRecommendedForStudent(Long studentId) {
        StudentProfile profile = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Профиль студента не найден: " + studentId));

        if (profile.getSpecialization() == null) {
            return new ArrayList<>();
        }

        return vacancyRepository.findAllByProfileSpecializationIdAndProfileStatus(
                        profile.getSpecialization().getId(), VacancyStatus.OPEN)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VacancyResponseDto> getVacanciesByCompany(Long companyId) {
        return vacancyRepository.findAllByCompanyId(companyId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VacancyResponseDto getVacancyResponseById(Long id) {
        return mapToResponseDto(getVacancyById(id));
    }

    @Transactional(readOnly = true)
    public boolean hasSpecialization(Long studentId) {
        return studentProfileRepository.findById(studentId)
                .map(profile -> profile.getSpecialization() != null)
                .orElse(false);
    }

    private Vacancy getVacancyById(Long id) {
        return vacancyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Вакансия не найдена: " + id));
    }

    @Transactional(readOnly = true)
    public SkillGapReport calculateSkillGap(Long studentId, Long vacancyId) {
        Vacancy vacancy = getVacancyById(vacancyId);

        Map<Long, Integer> studentSkillsMap = studentSkillRepository.findByStudentId(studentId)
                .stream()
                .collect(Collectors.toMap(
                        sk -> sk.getId().getSkillId(),
                        sk -> sk.getLevel()
                ));

        List<VacancySkill> missingSkills = new ArrayList<>();
        List<SkillLevelDifference> lowLevelSkills = new ArrayList<>();

        for (VacancySkill requiredSkill : vacancy.getSkills()) {
            Long skillId = requiredSkill.getId().getSkillId();
            Integer requiredLevel = requiredSkill.getLevel();
            Integer studentLevel = studentSkillsMap.get(skillId);

            if (studentLevel == null) {
                missingSkills.add(requiredSkill);
            } else if (studentLevel < requiredLevel) {
                lowLevelSkills.add(new SkillLevelDifference(
                        requiredSkill.getSkill().getName(),
                        studentLevel,
                        requiredLevel
                ));
            }
        }
        return new SkillGapReport(missingSkills, lowLevelSkills);
    }

    @Transactional
    public void deleteVacancy(Long vacancyId, Long currentCompanyId) {
        Vacancy vacancy = getVacancyById(vacancyId);

        if (!vacancy.getCompanyId().equals(currentCompanyId)) {
            throw new ResourceOwnershipException("У вас нет прав на удаление этой вакансии");
        }

        vacancyRepository.delete(vacancy);
    }

    @Transactional
    public void update(Long vacancyId, VacancyUpdateDto dto, Long companyId) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId).orElseThrow(() -> new EntityNotFoundException("Вакансия с таким id " + vacancyId + " не найдена"));

        if (!vacancy.getCompanyId().equals(companyId)) {
            throw new ResourceOwnershipException("У вас нет прав на редактирование этой вакансии");
        }

        vacancy.setName(dto.getName());

        VacancyProfile profile = vacancy.getProfile();
        profile.setSalary(dto.getSalary());
        profile.setWorkSchedule(dto.getWorkSchedule());
        profile.setStatus(dto.getStatus());

        vacancy.getSkills().clear();

        if (dto.getSkills() != null) {
            dto.getSkills().forEach(sDto -> {
                VacancySkill skill = VacancySkill.builder()
                        .id(new VacancySkillId(vacancy.getId(), sDto.getSkillId()))
                        .vacancy(vacancy)
                        .level(sDto.getLevel())
                        .build();
                vacancy.getSkills().add(skill);
            });
        }
    }

    @Transactional
    public void create(VacancyCreateDto dto, Long companyId) {
        Specialization specialization = specializationRepository.findById(dto.getSpecializationId())
                .orElseThrow(() -> new EntityNotFoundException("Специализация не найдена"));

        Vacancy vacancy = Vacancy.builder()
                .name(dto.getName())
                .companyId(companyId)
                .build();

        VacancyProfile profile = VacancyProfile.builder()
                .vacancy(vacancy)
                .specialization(specialization)
                .salary(dto.getSalary())
                .workSchedule(dto.getWorkSchedule())
                .status(VacancyStatus.OPEN)
                .build();

        vacancy.setProfile(profile);

        if (dto.getSkills() != null) {
            List<VacancySkill> skills = dto.getSkills().stream()
                    .map(sDto -> {
                        VacancySkill vs = new VacancySkill();
                        vs.setId(new VacancySkillId(null, sDto.getSkillId()));
                        vs.setVacancy(vacancy);
                        vs.setLevel(sDto.getLevel());
                        return vs;
                    }).collect(Collectors.toList());
            vacancy.setSkills(skills);
        }

        vacancyRepository.save(vacancy);
    }

    private VacancyResponseDto mapToResponseDto(Vacancy vacancy) {
        VacancyProfile profile = vacancy.getProfile();
        Specialization spec = profile.getSpecialization();

        return VacancyResponseDto.builder()
                .id(vacancy.getId())
                .name(vacancy.getName())
                .companyId(vacancy.getCompanyId())
                .salary(profile.getSalary())
                .workSchedule(profile.getWorkSchedule())
                .status(profile.getStatus())
                .language(spec.getLanguage())
                .direction(spec.getDirection())
                .fullSpecialization(spec.getLanguage() + " (" + spec.getDirection() + ")")
                .skills(vacancy.getSkills().stream()
                        .map(vs -> new SkillResponseDto(
                                vs.getSkill().getName(),
                                vs.getLevel()))
                        .collect(Collectors.toList()))
                .build();
    }
}