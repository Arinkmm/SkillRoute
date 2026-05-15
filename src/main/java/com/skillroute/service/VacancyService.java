package com.skillroute.service;

import com.skillroute.dto.request.AddSkillRequest;
import com.skillroute.dto.request.CreateVacancyRequest;
import com.skillroute.dto.request.UpdateVacancyRequest;
import com.skillroute.dto.response.SpecializationResponse;
import com.skillroute.dto.response.VacancyResponse;
import com.skillroute.dto.response.VacancySkillResponse;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.exception.ResourceOwnershipException;
import com.skillroute.model.*;
import com.skillroute.model.id.VacancySkillId;
import com.skillroute.properties.MessageProperties;
import com.skillroute.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final SpecializationRepository specializationRepository;
    private final SkillRepository skillRepository;
    private final MessageProperties messages;

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
    public List<VacancyResponse> getHighDemandVacancies(int minSkills) {
        return vacancyRepository.findHighDemandVacancies(minSkills)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VacancyResponse getVacancyById(Long id) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getVacancyNotFound()));
        return mapToResponseDto(vacancy);
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

        vacancy.getVacancySkills().clear();
        processSkills(vacancy, dto.getSkills());
    }

    @Transactional
    public void closeVacancy(Long vacancyId, Long companyId) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getVacancyNotFound()));

        if (!vacancy.getCompany().getId().equals(companyId)) {
            throw new ResourceOwnershipException(messages.getVacancy().getEditForbidden());
        }

        vacancy.getProfile().setStatus(VacancyStatus.CLOSE);
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

        Set<VacancySkill> skills = skillDtos.stream().map(sDto -> {
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
                .specialization(mapSpecialization(spec))
                .skills(vacancy.getVacancySkills().stream()
                        .map(vs -> new VacancySkillResponse(
                                vs.getSkill().getId(),
                                vs.getSkill().getName(),
                                vs.getLevel()))
                        .collect(Collectors.toList()))
                .build();
    }

    private SpecializationResponse mapSpecialization(Specialization specialization) {
        if (specialization == null) {
            return null;
        }

        return SpecializationResponse.builder()
                .id(specialization.getId())
                .direction(specialization.getDirection())
                .language(specialization.getLanguage())
                .build();
    }
}
