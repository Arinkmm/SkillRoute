package com.skillroute.controller;

import com.skillroute.security.CustomUserDetails;
import com.skillroute.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentChatController {
    private final ChatService chatService;

    @GetMapping("/chats")
    public String chatsPage(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("chats", chatService.getPreviews(user.getId()));
        return "student/chats";
    }

    @GetMapping("/chat/{id}")
    public String openChatPage(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long id, Model model) {
        model.addAttribute("chatData", chatService.getChatResponse(id, user.getId()));
        return "chat";
    }
}