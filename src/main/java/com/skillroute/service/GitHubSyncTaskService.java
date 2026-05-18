package com.skillroute.service;

import com.skillroute.dto.response.GitHubSyncResponse;
import com.skillroute.dto.response.GitHubSyncStatus;
import com.skillroute.mapper.GitHubSyncMapper;
import com.skillroute.properties.MessageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class GitHubSyncTaskService {
    private final GitHubSyncService syncService;
    private final MessageProperties messages;
    private final TaskExecutor taskExecutor;
    private final GitHubSyncMapper gitHubSyncMapper;
    private final ConcurrentMap<Long, GitHubSyncResponse> statuses = new ConcurrentHashMap<>();

    public GitHubSyncResponse start(Long accountId) {
        syncService.validateCanSync(accountId);

        GitHubSyncResponse current = statuses.get(accountId);
        if (current != null && current.isRunning()) {
            return current;
        }

        int initialConfirmedCount = syncService.countConfirmedByGitHub(accountId);
        GitHubSyncResponse started = gitHubSyncMapper.toResponse(messages.getUi().getGithubSyncStarted(), initialConfirmedCount, GitHubSyncStatus.RUNNING);
        statuses.put(accountId, started);

        CompletableFuture.supplyAsync(() -> syncService.syncSkills(accountId, count -> updateProgress(accountId, count)), taskExecutor::execute)
                .thenAccept(count -> {
                    int confirmedCount = syncService.countConfirmedByGitHub(accountId);
                    String msg = confirmedCount > 0 ? messages.getUi().getGithubSyncSuccess() : messages.getUi().getGithubSyncFailed();
                    statuses.put(accountId, gitHubSyncMapper.toResponse(msg, confirmedCount, GitHubSyncStatus.SUCCESS));
                })
                .exceptionally(ex -> {
                    log.error("Ошибка синхронизации для аккаунта: {}", accountId, ex);
                    String msg = (ex.getCause() != null && ex.getCause().getMessage() != null)
                            ? ex.getCause().getMessage()
                            : messages.getUi().getGithubSyncError();

                    statuses.put(accountId, gitHubSyncMapper.toResponse(msg, syncService.countConfirmedByGitHub(accountId), GitHubSyncStatus.FAILED));
                    return null;
                });

        return started;
    }

    public GitHubSyncResponse getStatus(Long accountId) {
        return statuses.getOrDefault(accountId, gitHubSyncMapper.toResponse("", syncService.countConfirmedByGitHub(accountId), GitHubSyncStatus.IDLE));
    }

    private void updateProgress(Long accountId, int confirmedCount) {
        statuses.put(accountId, gitHubSyncMapper.toResponse(messages.getUi().getGithubSyncStarted(), confirmedCount, GitHubSyncStatus.RUNNING));
    }
}
