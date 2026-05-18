package com.skillroute.mapper;

import com.skillroute.dto.request.UpdateCompanyRequest;
import com.skillroute.dto.response.CompanyProfileResponse;
import com.skillroute.model.CompanyProfile;
import org.springframework.stereotype.Component;

@Component
public class CompanyProfileMapper {

    public CompanyProfileResponse toResponse(CompanyProfile profile) {
        return CompanyProfileResponse.builder()
                .id(profile.getId())
                .email(profile.getAccount().getEmail())
                .companyName(profile.getCompanyName())
                .description(profile.getDescription())
                .websiteUrl(profile.getWebsiteUrl())
                .confirmed(profile.isConfirmed())
                .accountVerified(profile.getAccount().isVerified())
                .build();
    }

    public UpdateCompanyRequest toUpdateRequest(CompanyProfile profile) {
        return UpdateCompanyRequest.builder()
                .companyName(profile.getCompanyName())
                .description(profile.getDescription())
                .websiteUrl(profile.getWebsiteUrl())
                .build();
    }
}
