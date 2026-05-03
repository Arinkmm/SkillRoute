package com.skillroute.controller;

import com.skillroute.dto.request.MessageRequest;
import com.skillroute.dto.response.MessageResponse;
import com.skillroute.security.CustomUserDetails;
import com.skillroute.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRestController {
    private final ChatService chatService;

    @PostMapping("/{id}/send")
    public ResponseEntity<MessageResponse> send(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long id, @Valid @RequestBody MessageRequest request) {
        return ResponseEntity.ok(chatService.sendMessage(id, user.getId(), request));
    }
}