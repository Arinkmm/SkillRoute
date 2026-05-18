package com.skillroute.repository;

import com.skillroute.model.GitHubSyncJob;
import com.skillroute.model.GitHubSyncJobStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GitHubSyncJobRepository extends JpaRepository<GitHubSyncJob, Long> {
    Optional<GitHubSyncJob> findFirstByStudent_IdAndStatusInOrderByCreatedAtDesc(Long studentId,
                                                                                 Collection<GitHubSyncJobStatus> statuses);

    Optional<GitHubSyncJob> findFirstByStudent_IdOrderByCreatedAtDesc(Long studentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT job FROM GitHubSyncJob job
            JOIN FETCH job.student
            WHERE job.status = GitHubSyncJobStatus.PENDING
            AND (job.retryAfterAt IS NULL OR job.retryAfterAt <= :now)
            ORDER BY job.createdAt ASC
            """)
    List<GitHubSyncJob> findReadyPendingJobs(@Param("now") LocalDateTime now, Pageable pageable);

    @Modifying
    @Query("""
            UPDATE GitHubSyncJob job
            SET job.status = GitHubSyncJobStatus.PENDING,
                job.message = :message,
                job.retryAfterAt = NULL,
                job.updatedAt = :now
            WHERE job.status = GitHubSyncJobStatus.RUNNING
            AND job.updatedAt < :threshold
            """)
    int requeueStaleRunningJobs(@Param("threshold") LocalDateTime threshold,
                                @Param("message") String message,
                                @Param("now") LocalDateTime now);
}
