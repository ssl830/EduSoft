package org.example.edusoft.service.notification;

import org.example.edusoft.entity.notification.Notification;
import java.util.List;

/**
 * 通知服务接口
 */
public interface NotificationService {
    
    /**
     * 创建通知
     */
    void createNotification(Notification notification);

    /**
     * 获取用户的所有通知
     */
    List<Notification> getUserNotifications(Long userId);

    /**
     * 获取用户未读通知数量
     */
    int getUnreadCount(Long userId);

    /**
     * 标记通知为已读
     */
    void markAsRead(Long id);

    /**
     * 标记用户所有通知为已读
     */
    void markAllAsRead(Long userId);

    /**
     * 删除通知
     */
    void deleteNotification(Long id);

    /**
     * 根据ID获取通知
     */
    Notification getNotificationById(Long id);
} 