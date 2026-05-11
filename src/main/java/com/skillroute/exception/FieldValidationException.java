package com.skillroute.exception;

import java.util.Map;

public class FieldValidationException extends RuntimeException {
    private final Map<String, String> fields;

    public FieldValidationException(String message, Map<String, String> fields) {
        super(message);
        this.fields = fields;
    }

    public Map<String, String> getFields() {
        return fields;
    }
}
