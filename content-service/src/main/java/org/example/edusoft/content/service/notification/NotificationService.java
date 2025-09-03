package org.example.edusoft.content.service.notification;

import org.example.edusoft.content.entity.notification.Notification;
import java.util.List;
import java.util.Map;

/**
 * 通知服务接口
 */
public interface NotificationService {
    
    /**
     * 创建通知
     */
    void createNotification(Notification notification);

    /**
     * 批量创建通知
     */
    void createBatchNotifications(List<Notification> notifications);

    /**
     * 获取用户的所有通知
     */
    List<Notification> getUserNotifications(String userId);

    /**
     * 获取用户的分页通知
     */
    List<Notification> getUserNotificationsPaged(String userId, int page, int size);

    /**
     * 获取用户未读通知数量
     */
    int getUnreadCount(String userId);

    /**
     * 标记通知为已读
     */
    void markAsRead(Long id);

    /**
     * 标记用户所有通知为已读
     */
    void markAllAsRead(String userId);

    /**
     * 删除通知
     */
    void deleteNotification(Long id);

    /**
     * 根据ID获取通知
     */
    Notification getNotificationById(Long id);

    /**
     * 获取用户特定类型的通知
     */
    List<Notification> getUserNotificationsByType(String userId, String type);

    /**
     * 获取用户特定课程的通知
     */
    List<Notification> getUserNotificationsByCourse(String userId, Long courseId);

    /**
     * 获取用户特定班级的通知
     */
    List<Notification> getUserNotificationsByClass(String userId, Long classId);

    /**
     * 搜索用户通知
     */
    List<Notification> searchUserNotifications(String userId, String keyword);

    /**
     * 获取通知统计信息
     */
    Map<String, Object> getNotificationStats(String userId);

    /**
     * 发送系统通知
     */
    void sendSystemNotification(String title, String message, List<String> userIds);

    /**
     * 发送课程通知
     */
    void sendCourseNotification(String title, String message, Long courseId, List<String> userIds);

    /**
     * 发送班级通知
     */
    void sendClassNotification(String title, String message, Long classId, List<String> userIds);

    /**
     * 清理过期通知
     */
    void cleanupExpiredNotifications(int daysToKeep);
} 