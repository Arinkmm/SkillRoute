package com.skillroute.service;

import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.exception.GitHubRateLimitException;
import com.skillroute.mapper.GitHubSyncMapper;
import com.skillroute.model.GitHubSyncJob;
import com.skillroute.model.GitHubSyncJobStatus;
import com.skillroute.model.StudentProfile;
import com.skillroute.openapi.model.GitHubSyncResponseApi;
import com.skillroute.properties.GithubProperties;
import com.skillroute.properties.MessageProperties;
import com.skillroute.repository.GitHubSyncJobRepository;
import com.skillroute.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
@RequiredArgsConstructor
public class GitHubSyncTaskService {
    private final GitHubSyncService syncService;
    private final MessageProperties messages;
    private final GitHubSyncMapper gitHubSyncMapper;
    private final GitHubSyncJobRepository jobRepository;
    private final StudentProfileRepository profileRepository;
    private final GithubProperties githubProperties;
    private final TransactionTemplate transactionTemplate;
    private final AtomicBoolean workerBusy = new AtomicBoolean(false);

    @Transactional
    public GitHubSyncResponseApi start(Long accountId) {
        syncService.validateCanSync(accountId);

        Optional<GitHubSyncJob> currentJob = jobRepository.findFirstByStudent_IdAndStatusInOrderByCreatedAtDesc(accountId, List.of(GitHubSyncJobStatus.PENDING, GitHubSyncJobStatus.RUNNING));
        if (currentJob.isPresent()) {
            return toResponse(currentJob.get());
        }

        StudentProfile student = profileRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getStudentNotFound()));
        int confirmedCount = syncService.countConfirmedByGitHub(accountId);

        GitHubSyncJob job = GitHubSyncJob.builder()
                .student(student)
                .status(GitHubSyncJobStatus.PENDING)
                .message(messages.getUi().getGithubSyncQueued())
                .confirmedCount(confirmedCount)
                .build();

        return toResponse(jobRepository.save(job));
    }

    @Transactional(readOnly = true)
    public GitHubSyncResponseApi getStatus(Long accountId) {
        return jobRepository.findFirstByStudent_IdOrderByCreatedAtDesc(accountId)
                .map(this::toResponse)
                .orElseGet(() -> gitHubSyncMapper.toResponse("", syncService.countConfirmedByGitHub(accountId), "IDLE"));
    }

    @Scheduled(fixedDelayString = "${github.sync.worker-delay-millis:5000}")
    public void processQueue() {
        if (!workerBusy.compareAndSet(false, true)) {
            return;
        }

        try {
            requeueStaleRunningJobs();
            takeNextJob().ifPresent(this::runJob);
        } finally {
            workerBusy.set(false);
        }
    }

    private void requeueStaleRunningJobs() {
        transactionTemplate.executeWithoutResult(status -> {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime threshold = now.minusMinutes(githubProperties.getSync().getRunningTimeoutMinutes());
            jobRepository.requeueStaleRunningJobs(threshold, messages.getUi().getGithubSyncQueued(), now);
        });
    }

    private Optional<Long> takeNextJob() {
        return transactionTemplate.execute(status -> jobRepository.findReadyPendingJobs(LocalDateTime.now(), PageRequest.of(0, 1)).stream()
                .findFirst()
                .map(job -> {
                    job.setStatus(GitHubSyncJobStatus.RUNNING);
                    job.setMessage(messages.getUi().getGithubSyncStarted());
                    job.setStartedAt(LocalDateTime.now());
                    job.setRetryAfterAt(null);
                    return jobRepository.save(job).getId();
                }));
    }

    private void runJob(Long jobId) {
        Long accountId = transactionTemplate.execute(status -> jobRepository.findById(jobId)
                .map(job -> job.getStudent().getId())
                .orElse(null));

        if (accountId == null) {
            return;
        }

        try {
            syncService.syncSkills(accountId, count -> updateProgress(jobId, count));
            completeJob(jobId, accountId);
        } catch (GitHubRateLimitException e) {
            requeueAfterRateLimit(jobId, accountId, e);
        } catch (Exception e) {
            failJob(jobId, accountId, e);
        }
    }

    private void updateProgress(Long jobId, int confirmedCount) {
        transactionTemplate.executeWithoutResult(status -> jobRepository.findById(jobId).ifPresent(job -> {
            job.setConfirmedCount(confirmedCount);
            job.setMessage(messages.getUi().getGithubSyncStarted());
        }));
    }

    private void completeJob(Long jobId, Long accountId) {
        int confirmedCount = syncService.countConfirmedByGitHub(accountId);
        String message = confirmedCount > 0
                ? messages.getUi().getGithubSyncSuccess()
                : messages.getUi().getGithubSyncFailed();

        transactionTemplate.executeWithoutResult(status -> jobRepository.findById(jobId).ifPresent(job -> {
            job.setStatus(GitHubSyncJobStatus.SUCCESS);
            job.setConfirmedCount(confirmedCount);
            job.setMessage(message);
            job.setFinishedAt(LocalDateTime.now());
            job.setRetryAfterAt(null);
        }));
    }

    private void requeueAfterRateLimit(Long jobId, Long accountId, GitHubRateLimitException e) {
        LocalDateTime retryAfter = e.getRetryAfter();
        int confirmedCount = syncService.countConfirmedByGitHub(accountId);
        String message = messages.getUi().getGithubSyncWaiting().formatted(retryAfter.format(DateTimeFormatter.ofPattern("HH:mm")));

        transactionTemplate.executeWithoutResult(status -> jobRepository.findById(jobId).ifPresent(job -> {
            job.setStatus(GitHubSyncJobStatus.PENDING);
            job.setConfirmedCount(confirmedCount);
            job.setMessage(message);
            job.setRetryAfterAt(retryAfter);
        }));
    }

    private void failJob(Long jobId, Long accountId, Exception e) {
        log.error("Ошибка синхронизации GitHub для аккаунта: {}", accountId, e);

        String message = e.getMessage() == null || e.getMessage().isBlank()
                ? messages.getUi().getGithubSyncError()
                : e.getMessage();
        int confirmedCount = syncService.countConfirmedByGitHub(accountId);

        transactionTemplate.executeWithoutResult(status -> jobRepository.findById(jobId).ifPresent(job -> {
            job.setStatus(GitHubSyncJobStatus.FAILED);
            job.setConfirmedCount(confirmedCount);
            job.setMessage(message);
            job.setFinishedAt(LocalDateTime.now());
            job.setRetryAfterAt(null);
        }));
    }

    private GitHubSyncResponseApi toResponse(GitHubSyncJob job) {
        return gitHubSyncMapper.toResponse(resolveMessage(job), job.getConfirmedCount(), toApiStatus(job.getStatus()));
    }

    private String toApiStatus(GitHubSyncJobStatus status) {
        return switch (status) {
            case PENDING, RUNNING -> "RUNNING";
            case SUCCESS -> "SUCCESS";
            case FAILED -> "FAILED";
        };
    }

    private String resolveMessage(GitHubSyncJob job) {
        if (job.getMessage() != null && !job.getMessage().isBlank()) {
            return job.getMessage();
        }

        return job.isActive() ? messages.getUi().getGithubSyncQueued() : "";
    }
}
