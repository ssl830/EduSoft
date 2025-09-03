package org.example.edusoft.content.exception;

/**
 * 微服务调用异常
 */
public class ServiceCallException extends RuntimeException {
    
    private final String serviceName;
    private final int statusCode;
    
    public ServiceCallException(String serviceName, int statusCode, String message) {
        super(String.format("调用%s服务失败: %d - %s", serviceName, statusCode, message));
        this.serviceName = serviceName;
        this.statusCode = statusCode;
    }
    
    public ServiceCallException(String serviceName, String message) {
        super(String.format("调用%s服务失败: %s", serviceName, message));
        this.serviceName = serviceName;
        this.statusCode = -1;
    }
    
    public ServiceCallException(String serviceName, String message, Throwable cause) {
        super(String.format("调用%s服务失败: %s", serviceName, message), cause);
        this.serviceName = serviceName;
        this.statusCode = -1;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
}
