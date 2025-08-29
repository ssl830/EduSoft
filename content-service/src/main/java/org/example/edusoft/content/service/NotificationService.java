package org.example.edusoft.content.service;

import org.example.edusoft.content.entity.Notification;

import java.util.List;

public interface NotificationService {
    
    Notification createNotification(Notification notification);
    
    Notification getNotificationById(Long id);
    
    List<Notification> getNotificationsByUserId(Long userId);
    
    List<Notification> getUnreadNotifications(Long userId);
    
    List<Notification> getNotificationsByType(String type);
    
    List<Notification> getNotificationsByRelated(String relatedType, Long relatedId);
    
    void markAsRead(Long id);
    
    void markAllAsRead(Long userId);
    
    void expireNotification(Long id);
}
