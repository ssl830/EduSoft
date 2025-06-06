package com.bugvictims.demo11.exception;

import ch.qos.logback.core.util.StringUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.bugvictims.demo11.Pojo.*;
@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e){
        e.printStackTrace();
        return new Result().error(StringUtils.hasLength(e.getMessage())? e.getMessage():"操作失败");
    }
}
