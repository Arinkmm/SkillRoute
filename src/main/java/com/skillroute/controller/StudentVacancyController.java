package com.skillroute.controller;

import com.skillroute.model.Account;
import com.skillroute.service.AccountService;
import com.skillroute.service.StudentVacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vacancies/{id}/apply")
@RequiredArgsConstructor
public class StudentVacancyController {
    private final StudentVacancyService studentVacancyService;
    private final AccountService accountService;

    @PostMapping
    public String applyForVacancy(@PathVariable Long id,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {
        Account account = accountService.getAccount(userDetails.getUsername());
        studentVacancyService.applyToVacancy(account.getId(), id);

        redirectAttributes.addFlashAttribute("message", "Вы успешно откликнулись на вакансию!");
        return "redirect:/vacancies/" + id;
    }
}
