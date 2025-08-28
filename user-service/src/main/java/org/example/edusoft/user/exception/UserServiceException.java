package org.example.edusoft.user.exception;

/**
 * 用户服务异常类
 */
public class UserServiceException extends RuntimeException {
    
    private String errorCode;
    
    public UserServiceException(String message) {
        super(message);
    }
    
    public UserServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public UserServiceException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public UserServiceException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}

