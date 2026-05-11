package com.skillroute.exception;

public class PasswordResetTokenException extends RuntimeException {
    public PasswordResetTokenException(String message) {
        super(message);
    }
}
