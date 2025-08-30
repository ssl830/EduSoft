package org.example.edusoft.learning.exception;

import org.example.edusoft.learning.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "org.example.edusoft.learning.controller")
public class LearningExceptionHandler {

    @ExceptionHandler(LearningException.class)
    public Result<Object> handleLearningException(LearningException e) {
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        return Result.error("参数错误：" + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<Object> handleException(Exception e) {
        return Result.error("系统内部错误：" + e.getMessage());
    }
}
