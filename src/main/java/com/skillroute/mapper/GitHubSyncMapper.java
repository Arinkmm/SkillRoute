package com.skillroute.mapper;

import com.skillroute.openapi.model.GitHubSyncResponseApi;
import org.springframework.stereotype.Component;

@Component
public class GitHubSyncMapper {
    private static final String RUNNING_STATUS = "RUNNING";

    public GitHubSyncResponseApi toResponse(String message, int confirmedCount, String status) {
        GitHubSyncResponseApi response = new GitHubSyncResponseApi();
        response.setMessage(message);
        response.setConfirmedCount(confirmedCount);
        response.setStatus(status);
        response.setRunning(RUNNING_STATUS.equals(status));
        return response;
    }
}
