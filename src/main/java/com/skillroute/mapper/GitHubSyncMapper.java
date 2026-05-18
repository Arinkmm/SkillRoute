package com.skillroute.mapper;

import com.skillroute.dto.response.GitHubSyncResponse;
import com.skillroute.dto.response.GitHubSyncStatus;
import org.springframework.stereotype.Component;

@Component
public class GitHubSyncMapper {

    public GitHubSyncResponse toResponse(String message, int confirmedCount, GitHubSyncStatus status) {
        return GitHubSyncResponse.builder()
                .message(message)
                .confirmedCount(confirmedCount)
                .status(status)
                .running(status == GitHubSyncStatus.RUNNING)
                .build();
    }
}
