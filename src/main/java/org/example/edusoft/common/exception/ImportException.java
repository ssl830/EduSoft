package org.example.edusoft.common.exception;

public class ImportException extends RuntimeException {
    private Integer code;
    private String message;

    public ImportException(String message) {
        this(500, message);
    }

    public ImportException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
} 