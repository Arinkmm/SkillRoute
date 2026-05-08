package com.skillroute.exception;

public class GithubUrlNotFoundException extends RuntimeException {
    public GithubUrlNotFoundException(String message) {
        super(message);
    }
}