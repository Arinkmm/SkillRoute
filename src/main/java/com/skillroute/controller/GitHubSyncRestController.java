package com.skillroute.controller;

import com.skillroute.dto.response.SuccessResponse;
import com.skillroute.security.CustomUserDetails;
import com.skillroute.service.GitHubSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student/skills/github-sync")
@RequiredArgsConstructor

public class GitHubSyncRestController {
    private final GitHubSyncService syncService;

    @PostMapping
    public ResponseEntity<SuccessResponse> triggerSync(@AuthenticationPrincipal CustomUserDetails user) {
        syncService.syncSkills(user.getId());

        return ResponseEntity.ok(SuccessResponse.builder()
                .message("Синхронизация с GitHub прошла успешно! Ваши навыки обновлены")
                .build());
    }

}