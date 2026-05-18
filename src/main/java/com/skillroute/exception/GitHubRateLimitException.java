package com.skillroute.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GitHubRateLimitException extends RuntimeException {
    private final LocalDateTime retryAfter;

    public GitHubRateLimitException(String message, LocalDateTime retryAfter) {
        super(message);
        this.retryAfter = retryAfter;
    }
}
