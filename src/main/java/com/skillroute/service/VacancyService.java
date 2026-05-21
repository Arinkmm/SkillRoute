package com.skillroute.service;

import com.skillroute.dto.request.AddSkillRequest;
import com.skillroute.dto.request.CreateVacancyRequest;
import com.skillroute.dto.request.UpdateVacancyRequest;
import com.skillroute.dto.response.VacancyResponse;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.exception.ResourceOwnershipException;
import com.skillroute.mapper.VacancyMapper;
import com.skillroute.model.*;
import com.skillroute.model.id.VacancySkillId;
import com.skillroute.properties.MessageProperties;
import com.skillroute.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final SpecializationRepository specializationRepository;
    private final SkillRepository skillRepository;
    private final StudentVacancyRepository studentVacancyRepository;
    private final MessageProperties messages;
    private final VacancyMapper vacancyMapper;

    @Transactional(readOnly = true)
    public List<VacancyResponse> getVacanciesByCompany(Long companyId) {
        return vacancyRepository.findAllByCompanyIdAndProfileStatusIn(companyId, List.of(VacancyStatus.OPEN, VacancyStatus.IN_PROGRESS))
                .stream()
                .map(vacancyMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public VacancyResponse getVacancyById(Long id) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getVacancyNotFound()));
        return vacancyMapper.toResponse(vacancy);
    }

    @Transactional
    public void createVacancy(CreateVacancyRequest dto, Long companyId) {
        CompanyProfile company = companyProfileRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getCompanyNotFound()));

        Specialization spec = specializationRepository.findById(dto.getSpecializationId())
                .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getSpecializationNotFound()));

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
                .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getVacancyNotFound()));

        if (!vacancy.getCompany().getId().equals(companyId)) {
            throw new ResourceOwnershipException(messages.getVacancy().getEditForbidden());
        }

        vacancy.setName(dto.getName());

        VacancyProfile profile = vacancy.getProfile();
        if (dto.getSpecializationId() != null) {
            Specialization spec = specializationRepository.findById(dto.getSpecializationId()).orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getSpecializationNotFound()));
            profile.setSpecialization(spec);
        }
        profile.setSalary(dto.getSalary());
        profile.setWorkSchedule(dto.getWorkSchedule());
        profile.setStatus(dto.getStatus());

        syncSkills(vacancy, dto.getSkills());
    }

    @Transactional
    public void closeVacancy(Long vacancyId, Long companyId) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getVacancyNotFound()));

        if (!vacancy.getCompany().getId().equals(companyId)) {
            throw new ResourceOwnershipException(messages.getVacancy().getEditForbidden());
        }

        vacancy.getProfile().setStatus(VacancyStatus.CLOSE);
        rejectOpenApplications(vacancyId);
    }

    @Transactional
    public void deleteVacancy(Long vacancyId, Long currentCompanyId) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getVacancyNotFound()));

        if (!vacancy.getCompany().getId().equals(currentCompanyId)) {
            throw new ResourceOwnershipException(messages.getVacancy().getDeleteForbidden());
        }

        vacancyRepository.delete(vacancy);
    }

    private void processSkills(Vacancy vacancy, List<AddSkillRequest> skillDtos) {
        if (skillDtos == null || skillDtos.isEmpty()) return;

        Set<VacancySkill> skills = compactSkills(skillDtos).stream().map(sDto -> {
            Skill skill = skillRepository.findById(sDto.getSkillId())
                    .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getSkillNotFound()));

            return VacancySkill.builder()
                    .id(new VacancySkillId(vacancy.getId(), skill.getId()))
                    .vacancy(vacancy)
                    .skill(skill)
                    .level(sDto.getLevel())
                    .build();
        }).collect(Collectors.toSet());

        vacancy.getVacancySkills().addAll(skills);
    }

    private void syncSkills(Vacancy vacancy, List<AddSkillRequest> skillDtos) {
        Map<Long, AddSkillRequest> requestedSkills = compactSkills(skillDtos).stream()
                .collect(Collectors.toMap(AddSkillRequest::getSkillId, Function.identity()));

        vacancy.getVacancySkills().removeIf(vacancySkill -> !requestedSkills.containsKey(vacancySkill.getSkill().getId()));

        Map<Long, VacancySkill> existingSkills = vacancy.getVacancySkills().stream()
                .collect(Collectors.toMap(vacancySkill -> vacancySkill.getSkill().getId(), Function.identity()));

        requestedSkills.forEach((skillId, skillDto) -> {
            VacancySkill existingSkill = existingSkills.get(skillId);
            if (existingSkill != null) {
                existingSkill.setLevel(skillDto.getLevel());
                return;
            }

            Skill skill = skillRepository.findById(skillId)
                    .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getSkillNotFound()));

            vacancy.getVacancySkills().add(VacancySkill.builder()
                    .id(new VacancySkillId(vacancy.getId(), skill.getId()))
                    .vacancy(vacancy)
                    .skill(skill)
                    .level(skillDto.getLevel())
                    .build());
        });
    }

    private List<AddSkillRequest> compactSkills(List<AddSkillRequest> skillDtos) {
        if (skillDtos == null || skillDtos.isEmpty()) {
            return List.of();
        }

        return skillDtos.stream()
                .filter(skill -> skill.getSkillId() != null)
                .collect(Collectors.toMap(AddSkillRequest::getSkillId, Function.identity(), (first, second) -> second))
                .values()
                .stream()
                .toList();
    }

    private void rejectOpenApplications(Long vacancyId) {
        studentVacancyRepository.findAllByVacancyId(vacancyId).stream()
                .filter(application -> !isTerminal(application.getStatus()))
                .forEach(application -> application.setStatus(StudentVacancyStatus.REJECTED));
    }

    private boolean isTerminal(StudentVacancyStatus status) {
        return status == StudentVacancyStatus.ACCEPTED || status == StudentVacancyStatus.REJECTED;
    }
}
