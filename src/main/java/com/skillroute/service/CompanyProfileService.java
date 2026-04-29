package com.skillroute.service;

import com.skillroute.dto.response.CompanyProfileResponse;
import com.skillroute.dto.request.UpdateCompanyRequest;
import com.skillroute.event.AccountRegisteredEvent;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.model.CompanyProfile;
import com.skillroute.model.Role;
import com.skillroute.repository.CompanyProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyProfileService {
    private final CompanyProfileRepository companyProfileRepository;

    @EventListener
    public void handleAccountRegistration(AccountRegisteredEvent event) {
        if (event.getAccount().getRole() == Role.COMPANY) {
            CompanyProfile profile = new CompanyProfile();
            profile.setAccount(event.getAccount());
            companyProfileRepository.save(profile);
        }
    }

    @Transactional(readOnly = true)
    public CompanyProfileResponse getCompanyById(Long accountId) {
        return companyProfileRepository.findById(accountId)
                .map(this::mapToResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("Компания не найдена"));
    }

    @Transactional(readOnly = true)
    public List<CompanyProfileResponse> getAllCompanies() {
        return companyProfileRepository.findAll().stream().map(companyProfile -> CompanyProfileResponse.builder().companyName(companyProfile.getCompanyName()).description(companyProfile.getDescription()).build()).toList();
    }

    @Transactional
    public void updateProfile(Long id, UpdateCompanyRequest form) {
        CompanyProfile companyProfile = companyProfileRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Компания не найдена"));

        companyProfile.setCompanyName(form.getCompanyName());
        companyProfile.setDescription(form.getDescription());
        companyProfile.setWebsiteUrl(form.getWebsiteUrl());
    }

    private CompanyProfileResponse mapToResponseDto(CompanyProfile profile) {
        return CompanyProfileResponse.builder()
                .companyName(profile.getCompanyName())
                .description(profile.getDescription())
                .websiteUrl(profile.getWebsiteUrl())
                .build();
    }
}
