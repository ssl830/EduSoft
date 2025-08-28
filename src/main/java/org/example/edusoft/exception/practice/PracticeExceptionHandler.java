package org.example.edusoft.exception.practice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "org.example.edusoft.controller.practice")
public class PracticeExceptionHandler {

    @ExceptionHandler(PracticeException.class)
    public ResponseEntity<Map<String, Object>> handlePracticeException(PracticeException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", e.getCode());
        response.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", "PRACTICE_ILLEGAL_ARGUMENT");
        response.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", "PRACTICE_INTERNAL_ERROR");
        response.put("message", "练习模块发生内部错误");
        return ResponseEntity.internalServerError().body(response);
    }
} 