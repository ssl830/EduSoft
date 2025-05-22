package org.example.edusoft.exception;

import java.io.Serial;

/**
 * 存储配置异常
 *
 * @Author: sjy
 * @Date: 2025/5/17 
 */
public class StorageConfigException extends IException {

    @Serial
    private static final long serialVersionUID = 7993671808524980055L;

    public StorageConfigException() {
        super();
    } 

    public StorageConfigException(String message) {
        super(message);
    }
}
