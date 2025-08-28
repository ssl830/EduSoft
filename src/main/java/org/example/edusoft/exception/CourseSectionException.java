package org.example.edusoft.exception;

public class CourseSectionException extends RuntimeException {
    private String code;
    private String message;

    public CourseSectionException(String code, String message) {
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