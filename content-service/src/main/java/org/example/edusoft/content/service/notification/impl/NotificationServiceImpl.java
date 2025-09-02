package org.example.edusoft.content.service.notification.impl;

import org.example.edusoft.content.entity.notification.Notification;
import org.example.edusoft.content.mapper.notification.NotificationMapper;
import org.example.edusoft.content.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {
    
    @Autowired
    private NotificationMapper notificationMapper;
    
    @Override
    public List<Notification> getUserNotifications(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        return notificationMapper.selectByUserId(userId);
    }
    
    @Override
    public int getUnreadCount(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        Integer count = notificationMapper.countUnreadByUserId(userId);
        return count != null ? count : 0;
    }
    
    @Override
    public void markAsRead(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("通知ID不能为空");
        }
        
        int updated = notificationMapper.markAsRead(id);
        if (updated == 0) {
            throw new IllegalArgumentException("通知不存在");
        }
    }
    
    @Override
    public void markAllAsRead(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        notificationMapper.markAllAsReadByUserId(userId);
    }
    
    @Override
    public void deleteNotification(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("通知ID不能为空");
        }
        
        int deleted = notificationMapper.deleteById(id);
        if (deleted == 0) {
            throw new IllegalArgumentException("通知不存在");
        }
    }
}
