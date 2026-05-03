package com.skillroute.controller;

import com.skillroute.security.CustomUserDetails;
import com.skillroute.service.ApplicantService;
import com.skillroute.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyChatController {
    private final ChatService chatService;
    private final ApplicantService applicantService;

    @GetMapping("/chats")
    public String chatsPage(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("chats", chatService.getPreviews(user.getId()));
        return "company/chats";
    }

    @GetMapping("/chat/{id}")
    public String openChatPage(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long id, Model model) {
        model.addAttribute("chatData", chatService.getChatResponse(id, user.getId()));
        return "chat";
    }

    @PostMapping("/chat/{chatId}/reject")
    public String reject(@PathVariable Long chatId, @RequestParam Long studentId, @RequestParam Long vacancyId) {
        applicantService.rejectStudent(studentId, vacancyId);
        return "redirect:/company/chats";
    }

    @PostMapping("/chat/{chatId}/accept")
    public String accept(@PathVariable Long chatId, @RequestParam Long studentId, @RequestParam Long vacancyId) {
        applicantService.acceptStudent(studentId, vacancyId);
        return "redirect:/company/chats";
    }
}