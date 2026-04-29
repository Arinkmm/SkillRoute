package com.skillroute.controller;

import com.skillroute.dto.CompanyProfileResponse;
import com.skillroute.service.CompanyProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class IndexController {
    private final CompanyProfileService companyProfileService;

    @ModelAttribute("companies")
    public List<CompanyProfileResponse> getCompanies() {
        return companyProfileService.getAllCompanies();
    }

    @GetMapping
    public String indexPage() {
        return "index";
    }
}
