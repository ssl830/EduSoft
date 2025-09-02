package org.example.edusoft.content.service.notification;

import org.example.edusoft.content.entity.notification.Notification;
import java.util.List;

public interface NotificationService {
    
    /**
     * 获取用户的所有通知
     */
    List<Notification> getUserNotifications(Long userId);
    
    /**
     * 获取未读通知数量
     */
    int getUnreadCount(Long userId);
    
    /**
     * 标记通知为已读
     */
    void markAsRead(Long id);
    
    /**
     * 标记所有通知为已读
     */
    void markAllAsRead(Long userId);
    
    /**
     * 删除通知
     */
    void deleteNotification(Long id);
}
