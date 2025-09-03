package org.example.edusoft.content.exception;

/**
 * 通知相关异常类
 */
public class NotificationException extends RuntimeException {
    
    public NotificationException(String message) {
        super(message);
    }
    
    public NotificationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static NotificationException invalidTaskPriority() {
        return new NotificationException("无效的任务优先级");
    }
    
    public static NotificationException invalidDeadline() {
        return new NotificationException("截止时间不能早于当前时间");
    }
    
    public static NotificationException taskReminderNotFound() {
        return new NotificationException("任务提醒不存在");
    }
    
    public static NotificationException taskAlreadyCompleted() {
        return new NotificationException("任务已经完成");
    }
}

