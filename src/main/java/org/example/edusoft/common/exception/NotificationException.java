package org.example.edusoft.common.exception;

/**
 * 通知相关异常
 */
public class NotificationException extends BusinessException {
    
    public NotificationException(String message) {
        super(400, message);
    }
    
    public NotificationException(Integer code, String message) {
        super(code, message);
    }
    
    public static NotificationException notificationNotFound() {
        return new NotificationException(404, "通知不存在");
    }
    
    public static NotificationException taskReminderNotFound() {
        return new NotificationException(404, "任务提醒不存在");
    }
    
    public static NotificationException invalidNotificationType() {
        return new NotificationException(400, "无效的通知类型");
    }
    
    public static NotificationException invalidTaskPriority() {
        return new NotificationException(400, "无效的任务优先级");
    }
    
    public static NotificationException invalidDeadline() {
        return new NotificationException(400, "截止时间不能早于当前时间");
    }
    
    public static NotificationException taskAlreadyCompleted() {
        return new NotificationException(400, "任务已经完成");
    }
    
    public static NotificationException taskNotCompleted() {
        return new NotificationException(400, "任务尚未完成");
    }
} 