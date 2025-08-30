package org.example.edusoft.learning.exception;

public class LearningException extends RuntimeException {
    private final String code;
    private final String message;

    public LearningException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
