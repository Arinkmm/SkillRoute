package com.skillroute.exception;

public class MailSendMessageException extends RuntimeException {
    public MailSendMessageException(String message) {
        super(message);
    }
}