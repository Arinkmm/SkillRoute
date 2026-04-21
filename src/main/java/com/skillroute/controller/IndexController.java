package com.skillroute.controller;

import com.skillroute.service.CompanyProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class IndexController {
    private final CompanyProfileService companyProfileService;
    @GetMapping
    public String index(Model model) {
        model.addAttribute("companies", companyProfileService.getAllCompanies());
        return "index";
    }
}
