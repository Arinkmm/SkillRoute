package com.skillroute.controller;

import com.skillroute.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/verification")
@RequiredArgsConstructor
public class VerificationController {
    private final AccountService accountService;

    @GetMapping
    public String verifyAccount(@RequestParam String token, RedirectAttributes redirectAttributes) {
        boolean isVerified = accountService.verifyUser(token);
        if (isVerified) {
            return "verified";
        } else {
            redirectAttributes.addFlashAttribute("error", "Ссылка недействительна или срок её действия истек");
            return "redirect:/login";
        }
    }
}
