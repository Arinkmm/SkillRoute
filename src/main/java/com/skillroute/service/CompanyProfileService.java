package com.skillroute.service;

import com.skillroute.dto.CompanyDto;
import com.skillroute.dto.EditCompanyDto;
import com.skillroute.event.AccountRegisteredEvent;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.model.CompanyProfile;
import com.skillroute.model.Role;
import com.skillroute.model.StudentProfile;
import com.skillroute.repository.CompanyProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;

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
    public CompanyProfile getCompanyById(Long id) {
        return companyProfileRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Компания с таким id " + id + " не найдена"));
    }

    @Transactional(readOnly = true)
    public List<CompanyDto> getAllCompanies() {
        return companyProfileRepository.findAll().stream().map(companyProfile -> CompanyDto.builder().companyName(companyProfile.getCompanyName()).description(companyProfile.getDescription()).build()).toList();
    }

    @Transactional
    public void updateProfile(Long id, EditCompanyDto form) {
        CompanyProfile companyProfile = companyProfileRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Профиль компании с таким id " + id + " не найден"));

        companyProfile.setCompanyName(form.getCompanyName());
        companyProfile.setDescription(form.getDescription());
        companyProfile.setWebsiteUrl(form.getWebsiteUrl());
    }
}
