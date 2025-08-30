package org.example.edusoft.content.dto;

import lombok.Data;

@Data
public class Result<T> {
    private boolean success;
    private String message;
    private T data;
    private Integer code;

    public static <T> Result<T> ok(T data, String message) {
        Result<T> result = new Result<>();
        result.success = true;
        result.message = message;
        result.data = data;
        result.code = 200;
        return result;
    }

    public static <T> Result<T> ok(T data) {
        return ok(data, "操作成功");
    }

    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.success = false;
        result.message = message;
        result.code = 500;
        return result;
    }

    public static <T> Result<T> error(String message, Integer code) {
        Result<T> result = new Result<>();
        result.success = false;
        result.message = message;
        result.code = code;
        return result;
    }
}
