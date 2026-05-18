package com.skillroute.controller;

import com.skillroute.openapi.model.GitHubSyncResponseApi;
import com.skillroute.security.CustomUserDetails;
import com.skillroute.service.GitHubSyncTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student/skills/github-sync")
@RequiredArgsConstructor
public class GitHubSyncRestController {
    private final GitHubSyncTaskService syncTaskService;

    @PostMapping
    public ResponseEntity<GitHubSyncResponseApi> triggerSync(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(syncTaskService.start(user.getId()));
    }

    @GetMapping("/status")
    public ResponseEntity<GitHubSyncResponseApi> getStatus(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(syncTaskService.getStatus(user.getId()));
    }
}
