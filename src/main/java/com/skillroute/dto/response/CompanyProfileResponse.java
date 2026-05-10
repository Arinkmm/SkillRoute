package com.skillroute.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyProfileResponse {
    private Long id;
    private String email;
    private String companyName;
    private String description;
    private String websiteUrl;
    private boolean confirmed;
    private boolean accountVerified;
}
