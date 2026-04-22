package com.skillroute.controller;

import com.skillroute.dto.PasswordChangeDto;
import com.skillroute.exception.InvalidPasswordException;
import com.skillroute.model.Account;
import com.skillroute.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile/edit/password")
@RequiredArgsConstructor
public class EditPasswordController {
    private final AccountService accountService;

    @GetMapping
    public String editPasswordPage() {
        return "edit_password";
    }

    @PostMapping
    public String editPassword(@AuthenticationPrincipal UserDetails userDetails, @ModelAttribute PasswordChangeDto form, Model model, RedirectAttributes redirectAttributes) {
        Account account = accountService.getAccount(userDetails.getUsername());
        try {
            accountService.changePassword(account.getId(), form);
            redirectAttributes.addFlashAttribute("message", "Пароль успешно обновлён!");
        } catch (InvalidPasswordException e) {
            model.addAttribute("error", e.getMessage());
            return "edit_password";
        }
        return "redirect:/profile";
    }
}
