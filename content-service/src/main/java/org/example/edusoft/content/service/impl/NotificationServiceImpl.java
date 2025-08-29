package org.example.edusoft.content.service.impl;

import org.example.edusoft.content.entity.Notification;
import org.example.edusoft.content.mapper.NotificationMapper;
import org.example.edusoft.content.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public Notification createNotification(Notification notification) {
        notificationMapper.insert(notification);
        return notification;
    }

    @Override
    public Notification getNotificationById(Long id) {
        return notificationMapper.findById(id);
    }

    @Override
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationMapper.findByUserId(userId);
    }

    @Override
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationMapper.findUnreadByUserId(userId);
    }

    @Override
    public List<Notification> getNotificationsByType(String type) {
        return notificationMapper.findByType(type);
    }

    @Override
    public List<Notification> getNotificationsByRelated(String relatedType, Long relatedId) {
        return notificationMapper.findByRelated(relatedType, relatedId);
    }

    @Override
    public void markAsRead(Long id) {
        notificationMapper.markAsRead(id);
    }

    @Override
    public void markAllAsRead(Long userId) {
        notificationMapper.markAllAsRead(userId);
    }

    @Override
    public void expireNotification(Long id) {
        notificationMapper.expireById(id);
    }
}

