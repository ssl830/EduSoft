package org.example.edusoft.exception.practice;

public class PracticeException extends RuntimeException {
    private final String code;
    private final String message;

    public PracticeException(String code, String message) {
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