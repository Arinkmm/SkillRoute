package com.skillroute.service;

import com.skillroute.dto.response.CompanyProfileResponse;
import com.skillroute.dto.request.UpdateCompanyRequest;
import com.skillroute.event.AccountRegisteredEvent;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.mapper.CompanyProfileMapper;
import com.skillroute.model.CompanyProfile;
import com.skillroute.model.Role;
import com.skillroute.properties.MessageProperties;
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
    private final MessageProperties messages;
    private final CompanyProfileMapper companyProfileMapper;

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
                .map(companyProfileMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getCompanyNotFound()));
    }

    @Transactional(readOnly = true)
    public UpdateCompanyRequest getUpdateForm(Long accountId) {
        CompanyProfile profile = companyProfileRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getCompanyNotFound()));

        return companyProfileMapper.toUpdateRequest(profile);
    }

    @Transactional(readOnly = true)
    public List<CompanyProfileResponse> getConfirmedCompanies() {
        return companyProfileRepository.findAllConfirmed().stream()
                .map(companyProfileMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CompanyProfileResponse> getPendingCompanies() {
        return companyProfileRepository.findAllPending().stream()
                .map(companyProfileMapper::toResponse)
                .toList();
    }

    @Transactional
    public void updateProfile(Long id, UpdateCompanyRequest form) {
        CompanyProfile companyProfile = companyProfileRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getCompanyNotFound()));

        companyProfile.setCompanyName(normalizeBlank(form.getCompanyName()));
        companyProfile.setDescription(normalizeBlank(form.getDescription()));
        companyProfile.setWebsiteUrl(normalizeBlank(form.getWebsiteUrl()));
    }

    @Transactional
    public void approveCompany(Long companyId) {
        CompanyProfile companyProfile = companyProfileRepository.findById(companyId).orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getCompanyNotFound()));

        companyProfile.setConfirmed(true);
    }

    @Transactional(readOnly = true)
    public boolean isConfirmed(Long companyId) {
        return companyProfileRepository.findById(companyId)
                .map(CompanyProfile::isConfirmed)
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean isProfileComplete(Long companyId) {
        return companyProfileRepository.findById(companyId)
                .map(profile -> hasText(profile.getCompanyName()))
                .orElse(false);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String normalizeBlank(String value) {
        return hasText(value) ? value.trim() : null;
    }
}
