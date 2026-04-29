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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final SpecializationRepository specializationRepository;
    private final SkillRepository skillRepository;

    @Transactional(readOnly = true)
    public List<VacancyResponse> getAllActive() {
        return vacancyRepository.findAllByProfileStatus(VacancyStatus.OPEN)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VacancyResponse> getVacanciesByCompany(Long companyId) {
        return vacancyRepository.findAllByCompanyId(companyId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VacancyResponse getVacancyById(Long id) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Вакансия не найдена"));
        return mapToResponseDto(vacancy);
    }

    @Transactional
    public void createVacancy(CreateVacancyRequest dto, Long companyId) {
        CompanyProfile company = companyProfileRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Компания не найдена"));

        Specialization spec = specializationRepository.findById(dto.getSpecializationId())
                .orElseThrow(() -> new EntityNotFoundException("Специализация не найдена"));

        Vacancy vacancy = Vacancy.builder()
                .name(dto.getName())
                .company(company)
                .build();

        VacancyProfile profile = VacancyProfile.builder()
                .vacancy(vacancy)
                .specialization(spec)
                .salary(dto.getSalary())
                .workSchedule(dto.getWorkSchedule())
                .status(VacancyStatus.OPEN)
                .build();

        vacancy.setProfile(profile);

        Vacancy savedVacancy = vacancyRepository.save(vacancy);
        processSkills(savedVacancy, dto.getSkills());
    }

    @Transactional
    public void updateVacancy(Long vacancyId, UpdateVacancyRequest dto, Long companyId) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new EntityNotFoundException("Вакансия не найдена"));

        if (!vacancy.getCompany().getId().equals(companyId)) {
            throw new ResourceOwnershipException("У вас нет прав на редактирование этой вакансии");
        }

        vacancy.setName(dto.getName());

        VacancyProfile profile = vacancy.getProfile();
        profile.setSalary(dto.getSalary());
        profile.setWorkSchedule(dto.getWorkSchedule());
        profile.setStatus(dto.getStatus());

        vacancy.getVacancySkills().clear();
        processSkills(vacancy, dto.getSkills());
    }

    @Transactional
    public void deleteVacancy(Long vacancyId, Long currentCompanyId) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new EntityNotFoundException("Вакансия не найдена"));

        if (!vacancy.getCompany().getId().equals(currentCompanyId)) {
            throw new ResourceOwnershipException("У вас нет прав на удаление этой вакансии");
        }

        vacancyRepository.delete(vacancy);
    }

    private void processSkills(Vacancy vacancy, List<AddSkillRequest> skillDtos) {
        if (skillDtos == null || skillDtos.isEmpty()) return;

        Set<VacancySkill> skills = skillDtos.stream().map(sDto -> {
            Skill skill = skillRepository.findById(sDto.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Навык не найден"));

            return VacancySkill.builder()
                    .id(new VacancySkillId(vacancy.getId(), skill.getId()))
                    .vacancy(vacancy)
                    .skill(skill)
                    .level(sDto.getLevel())
                    .build();
        }).collect(Collectors.toSet());

        vacancy.getVacancySkills().addAll(skills);
    }

    private VacancyResponse mapToResponseDto(Vacancy vacancy) {
        VacancyProfile profile = vacancy.getProfile();
        Specialization spec = profile.getSpecialization();

        return VacancyResponse.builder()
                .id(vacancy.getId())
                .name(vacancy.getName())
                .companyId(vacancy.getCompany().getId())
                .salary(profile.getSalary())
                .workSchedule(profile.getWorkSchedule())
                .status(profile.getStatus())
                .language(spec.getLanguage())
                .direction(spec.getDirection())
                .fullSpecialization(spec.getLanguage() + " (" + spec.getDirection() + ")")
                .skills(vacancy.getVacancySkills().stream()
                        .map(vs -> new VacancySkillResponse(
                                vs.getSkill().getId(),
                                vs.getSkill().getName(),
                                vs.getLevel()))
                        .collect(Collectors.toList()))
                .build();
    }
}