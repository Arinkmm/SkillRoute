package com.skillroute.controller;

import com.skillroute.openapi.model.ChatResponseApi;
import com.skillroute.openapi.model.MessageRequestApi;
import com.skillroute.openapi.model.MessageResponseApi;
import com.skillroute.security.CustomUserDetails;
import com.skillroute.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRestController {
    private final ChatService chatService;

    @GetMapping("/{id}/messages")
    public ResponseEntity<ChatResponseApi> messages(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long id) {
        return ResponseEntity.ok(chatService.getChatResponse(id, user.getId()));
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<MessageResponseApi> send(@AuthenticationPrincipal CustomUserDetails user,
                                                   @PathVariable Long id,
                                                   @Valid @RequestBody MessageRequestApi request) {
        return ResponseEntity.ok(chatService.sendMessage(id, user.getId(), request));
    }
}
