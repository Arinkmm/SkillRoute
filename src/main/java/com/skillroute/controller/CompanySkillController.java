package com.skillroute.controller;

import com.skillroute.dto.request.AddResourceRequest;
import com.skillroute.service.ResourceService;
import com.skillroute.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/company/skills")
@RequiredArgsConstructor
public class CompanySkillController {
    private final SkillService  skillService;
    private final ResourceService resourceService;

    @GetMapping
    public String skillsPage(Model model) {
        model.addAttribute("skills", skillService.getSkills());
        return "company/skills";
    }

    @GetMapping("/{id}")
    public String viewSkill(@PathVariable Long id, Model model) {
        model.addAttribute("skill", skillService.getSkillById(id));
        return "company/skills-details";
    }

    @GetMapping("/{id}/resources")
    public String addResourceForm(@PathVariable Long id, Model model) {
        model.addAttribute("skillId", id);
        model.addAttribute("addResourceForm", new AddResourceRequest());
        return "company/add-resource";
    }

    @PostMapping("/{id}/resources")
    public String addResource(@PathVariable Long id,
                              @Valid @ModelAttribute AddResourceRequest form,
                              RedirectAttributes redirectAttributes) {
        resourceService.addResourceToSkill(id, form);
        redirectAttributes.addFlashAttribute("success", "Материал добавлен!");
        return "redirect:/company/skills/" + id;
    }
}
