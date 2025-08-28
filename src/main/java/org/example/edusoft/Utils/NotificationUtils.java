package org.example.edusoft.utils;

import org.example.edusoft.entity.notification.Notification;
import org.example.edusoft.entity.notification.NotificationType;
import org.example.edusoft.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知工具类
 * 用于创建各种类型的通知，包括：
 * 1. 练习通知 - 当老师发布新练习时
 * 2. 作业通知 - 当老师发布新作业时
 * 3. 任务通知 - 当老师发布新任务时
 * 4. DDL提醒 - 当任务截止时间前3天时
 */
@Component
public class NotificationUtils {

    @Autowired
    private NotificationService notificationService;

    /**
     * 创建练习通知
     */
    public void createPracticeNotification(Long practiceId, String practiceTitle, Long courseId, Long classId, List<Long> studentIds) {
        for (Long studentId : studentIds) {
            Notification notification = new Notification();
            notification.setUserId(studentId);
            notification.setTitle("新练习提醒");
            notification.setMessage(String.format("老师发布了新的在线练习：%s", practiceTitle));
            notification.setType(NotificationType.PRACTICE);
            notification.setReadFlag(false);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setRelatedId(practiceId);
            notification.setRelatedType("PRACTICE");
            
            notificationService.createNotification(notification);
        }
    }

    /**
     * 创建作业通知
     */
    public void createHomeworkNotification(Long homeworkId, String homeworkTitle, Long courseId, Long classId, List<Long> studentIds) {
        for (Long studentId : studentIds) {
            Notification notification = new Notification();
            notification.setUserId(studentId);
            notification.setTitle("新作业提醒");
            notification.setMessage(String.format("老师发布了新的作业：%s", homeworkTitle));
            notification.setType(NotificationType.HOMEWORK);
            notification.setReadFlag(false);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setRelatedId(homeworkId);
            notification.setRelatedType("HOMEWORK");
            
            notificationService.createNotification(notification);
        }
    }

    /**
     * 创建任务通知
     */
    public void createTaskNotification(Long taskId, String taskTitle, Long courseId, Long classId, List<Long> studentIds) {
        for (Long studentId : studentIds) {
            Notification notification = new Notification();
            notification.setUserId(studentId);
            notification.setTitle("新任务提醒");
            notification.setMessage(String.format("老师发布了新的任务：%s", taskTitle));
            notification.setType(NotificationType.TASK);
            notification.setReadFlag(false);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setRelatedId(taskId);
            notification.setRelatedType("TASK");
            
            notificationService.createNotification(notification);
        }
    }
} 