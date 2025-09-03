package org.example.edusoft.content.service.notification.impl;

import org.example.edusoft.content.entity.notification.Notification;
import org.example.edusoft.content.mapper.notification.NotificationMapper;
import org.example.edusoft.content.service.notification.NotificationService;
import org.example.edusoft.content.client.UserClient;
import org.example.edusoft.content.client.CourseClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 通知服务实现类
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserClient userClient;

    @Autowired
    private CourseClient courseClient;

    @Override
    @Transactional
    public void createNotification(Notification notification) {
        // 设置默认值
        if (notification.getCreatedAt() == null) {
            notification.setCreatedAt(LocalDateTime.now());
        }
        if (notification.getUpdatedAt() == null) {
            notification.setUpdatedAt(LocalDateTime.now());
        }
        if (notification.getReadFlag() == null) {
            notification.setReadFlag(false);
        }
        if (notification.getStatus() == null) {
            notification.setStatus("active");
        }
        if (notification.getPriority() == null) {
            notification.setPriority("normal");
        }
        
        // 如果有关联的课程或班级，获取相关信息
        enrichNotificationWithRelatedInfo(notification);
        
        notificationMapper.insert(notification);
    }

    @Override
    @Transactional
    public void createBatchNotifications(List<Notification> notifications) {
        if (notifications != null && !notifications.isEmpty()) {
            // 为每个通知设置默认值
            notifications.forEach(notification -> {
                if (notification.getCreatedAt() == null) {
                    notification.setCreatedAt(LocalDateTime.now());
                }
                if (notification.getUpdatedAt() == null) {
                    notification.setUpdatedAt(LocalDateTime.now());
                }
                if (notification.getReadFlag() == null) {
                    notification.setReadFlag(false);
                }
                if (notification.getStatus() == null) {
                    notification.setStatus("active");
                }
                if (notification.getPriority() == null) {
                    notification.setPriority("normal");
                }
                
                // 如果有关联的课程或班级，获取相关信息
                enrichNotificationWithRelatedInfo(notification);
            });
            
            notificationMapper.batchInsert(notifications);
        }
    }

    @Override
    public List<Notification> getUserNotifications(String userId) {
        return notificationMapper.findByUserId(userId);
    }

    @Override
    public List<Notification> getUserNotificationsPaged(String userId, int page, int size) {
        int offset = (page - 1) * size;
        return notificationMapper.findByUserIdPaged(userId, offset, size);
    }

    @Override
    public int getUnreadCount(String userId) {
        return notificationMapper.countUnreadByUserId(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        notificationMapper.markAsRead(id);
    }

    @Override
    @Transactional
    public void markAllAsRead(String userId) {
        notificationMapper.markAllAsRead(userId);
    }

    @Override
    @Transactional
    public void deleteNotification(Long id) {
        notificationMapper.softDeleteById(id);
    }

    @Override
    public Notification getNotificationById(Long id) {
        return notificationMapper.findById(id);
    }

    @Override
    public List<Notification> getUserNotificationsByType(String userId, String type) {
        return notificationMapper.findByUserIdAndType(userId, type);
    }

    @Override
    public List<Notification> getUserNotificationsByCourse(String userId, Long courseId) {
        return notificationMapper.findByUserIdAndCourse(userId, courseId);
    }

    @Override
    public List<Notification> getUserNotificationsByClass(String userId, Long classId) {
        return notificationMapper.findByUserIdAndClass(userId, classId);
    }

    @Override
    public List<Notification> searchUserNotifications(String userId, String keyword) {
        return notificationMapper.searchByUserIdAndKeyword(userId, keyword);
    }

    @Override
    public Map<String, Object> getNotificationStats(String userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取总通知数量
        int totalCount = notificationMapper.countByUserId(userId);
        stats.put("totalCount", totalCount);
        
        // 获取未读通知数量
        int unreadCount = notificationMapper.countUnreadByUserId(userId);
        stats.put("unreadCount", unreadCount);
        
        // 获取已读通知数量
        stats.put("readCount", totalCount - unreadCount);
        
        // 获取各类型通知统计
        List<Map<String, Object>> typeStats = notificationMapper.getTypeStatsByUserId(userId);
        stats.put("typeStats", typeStats);
        
        return stats;
    }

    @Override
    @Transactional
    public void sendSystemNotification(String title, String message, List<String> userIds) {
        List<Notification> notifications = new ArrayList<>();
        
        for (String userId : userIds) {
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setType(org.example.edusoft.content.entity.notification.NotificationType.SYSTEM);
            notification.setReadFlag(false);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setUpdatedAt(LocalDateTime.now());
            notification.setPriority("normal");
            notification.setStatus("active");
            notification.setRelatedType("SYSTEM");
            
            notifications.add(notification);
        }
        
        if (!notifications.isEmpty()) {
            createBatchNotifications(notifications);
        }
    }

    @Override
    @Transactional
    public void sendCourseNotification(String title, String message, Long courseId, List<String> userIds) {
        try {
            // 获取课程信息
            Map<String, Object> courseInfo = courseClient.getCourseInfo(courseId);
            String courseName = (String) courseInfo.get("courseName");
            
            List<Notification> notifications = new ArrayList<>();
            
            for (String userId : userIds) {
                Notification notification = new Notification();
                notification.setUserId(userId);
                notification.setTitle(title);
                notification.setMessage(message);
                notification.setType(org.example.edusoft.content.entity.notification.NotificationType.COURSE_NOTICE);
                notification.setReadFlag(false);
                notification.setCreatedAt(LocalDateTime.now());
                notification.setUpdatedAt(LocalDateTime.now());
                notification.setPriority("normal");
                notification.setStatus("active");
                notification.setRelatedId(courseId);
                notification.setRelatedType("COURSE");
                notification.setCourseId(courseId);
                notification.setCourseName(courseName);
                
                notifications.add(notification);
            }
            
            if (!notifications.isEmpty()) {
                createBatchNotifications(notifications);
            }
        } catch (Exception e) {
            // 记录日志但不抛出异常，避免影响主流程
            System.err.println("发送课程通知失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void sendClassNotification(String title, String message, Long classId, List<String> userIds) {
        try {
            // 获取班级信息
            Map<String, Object> classInfo = courseClient.getClassInfo(classId);
            String className = (String) classInfo.get("className");
            Long courseId = (Long) classInfo.get("courseId");
            
            // 获取课程信息
            Map<String, Object> courseInfo = courseClient.getCourseInfo(courseId);
            String courseName = (String) courseInfo.get("courseName");
            
            List<Notification> notifications = new ArrayList<>();
            
            for (String userId : userIds) {
                Notification notification = new Notification();
                notification.setUserId(userId);
                notification.setTitle(title);
                notification.setMessage(message);
                notification.setType(org.example.edusoft.content.entity.notification.NotificationType.ANNOUNCEMENT);
                notification.setReadFlag(false);
                notification.setCreatedAt(LocalDateTime.now());
                notification.setUpdatedAt(LocalDateTime.now());
                notification.setPriority("normal");
                notification.setStatus("active");
                notification.setRelatedId(classId);
                notification.setRelatedType("CLASS");
                notification.setClassId(classId);
                notification.setClassName(className);
                notification.setCourseId(courseId);
                notification.setCourseName(courseName);
                
                notifications.add(notification);
            }
            
            if (!notifications.isEmpty()) {
                createBatchNotifications(notifications);
            }
        } catch (Exception e) {
            // 记录日志但不抛出异常，避免影响主流程
            System.err.println("发送班级通知失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void cleanupExpiredNotifications(int daysToKeep) {
        notificationMapper.deleteExpiredNotifications(daysToKeep);
    }

    /**
     * 丰富通知信息，添加相关的课程、班级、发送者等信息
     */
    private void enrichNotificationWithRelatedInfo(Notification notification) {
        try {
            // 如果有关联的课程ID，获取课程信息
            if (notification.getCourseId() != null) {
                Map<String, Object> courseInfo = courseClient.getCourseInfo(notification.getCourseId());
                if (courseInfo != null && courseInfo.get("courseName") != null) {
                    notification.setCourseName((String) courseInfo.get("courseName"));
                }
            }
            
            // 如果有关联的班级ID，获取班级信息
            if (notification.getClassId() != null) {
                Map<String, Object> classInfo = courseClient.getClassInfo(notification.getClassId());
                if (classInfo != null && classInfo.get("className") != null) {
                    notification.setClassName((String) classInfo.get("className"));
                }
            }
            
            // 如果有发送者ID，获取发送者信息
            if (notification.getSenderId() != null) {
                Map<String, Object> userInfo = userClient.getUserInfo(notification.getSenderId());
                if (userInfo != null && userInfo.get("userName") != null) {
                    notification.setSenderName((String) userInfo.get("userName"));
                }
            }
        } catch (Exception e) {
            // 记录日志但不抛出异常，避免影响主流程
            System.err.println("丰富通知信息失败: " + e.getMessage());
        }
    }
} 