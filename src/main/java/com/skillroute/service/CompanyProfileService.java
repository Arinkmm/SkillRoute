package com.skillroute.service;

import com.skillroute.dto.CompanyDto;
import com.skillroute.model.CompanyProfile;
import com.skillroute.repository.CompanyProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyProfileService {
    private final CompanyProfileRepository companyProfileRepository;

    @Transactional(readOnly = true)
    public List<CompanyDto> getAllCompanies() {
        return companyProfileRepository.findAll().stream().map(companyProfile -> CompanyDto.builder().companyName(companyProfile.getCompanyName()).description(companyProfile.getDescription()).build()).toList();
    }
}
