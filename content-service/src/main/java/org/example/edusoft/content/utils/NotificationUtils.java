package org.example.edusoft.content.utils;

import org.example.edusoft.content.entity.notification.Notification;
import org.example.edusoft.content.entity.notification.NotificationType;
import org.example.edusoft.content.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * 通知工具类
 * 用于创建各种类型的通知，包括：
 * 1. 练习通知 - 当老师发布新练习时
 * 2. 作业通知 - 当老师发布新作业时
 * 3. 任务通知 - 当老师发布新任务时
 * 4. DDL提醒 - 当任务截止时间前3天时
 * 5. 资源上传通知 - 当老师上传新资源时
 * 6. 讨论回复通知 - 当有人回复讨论时
 * 7. 课程通知 - 课程相关的通知
 * 8. 系统通知 - 系统级别的通知
 */
@Component
public class NotificationUtils {

    @Autowired
    private NotificationService notificationService;

    /**
     * 创建练习通知
     */
    public void createPracticeNotification(Long practiceId, String practiceTitle, Long courseId, Long classId, List<String> studentIds) {
        List<Notification> notifications = new ArrayList<>();
        
        for (String studentId : studentIds) {
            Notification notification = new Notification();
            notification.setUserId(studentId);
            notification.setTitle("新练习提醒");
            notification.setMessage(String.format("老师发布了新的在线练习：%s", practiceTitle));
            notification.setType(NotificationType.PRACTICE);
            notification.setReadFlag(false);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setUpdatedAt(LocalDateTime.now());
            notification.setRelatedId(practiceId);
            notification.setRelatedType("PRACTICE");
            notification.setCourseId(courseId);
            notification.setClassId(classId);
            notification.setPriority("normal");
            notification.setStatus("active");
            
            notifications.add(notification);
        }
        
        if (!notifications.isEmpty()) {
            notificationService.createBatchNotifications(notifications);
        }
    }

    /**
     * 创建作业通知
     */
    public void createHomeworkNotification(Long homeworkId, String homeworkTitle, Long courseId, Long classId, List<String> studentIds) {
        List<Notification> notifications = new ArrayList<>();
        
        for (String studentId : studentIds) {
            Notification notification = new Notification();
            notification.setUserId(studentId);
            notification.setTitle("新作业提醒");
            notification.setMessage(String.format("老师发布了新的作业：%s", homeworkTitle));
            notification.setType(NotificationType.HOMEWORK);
            notification.setReadFlag(false);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setUpdatedAt(LocalDateTime.now());
            notification.setRelatedId(homeworkId);
            notification.setRelatedType("HOMEWORK");
            notification.setCourseId(courseId);
            notification.setClassId(classId);
            notification.setPriority("high");
            notification.setStatus("active");
            
            notifications.add(notification);
        }
        
        if (!notifications.isEmpty()) {
            notificationService.createBatchNotifications(notifications);
        }
    }

    /**
     * 创建任务通知
     */
    public void createTaskNotification(Long taskId, String taskTitle, Long courseId, Long classId, List<String> studentIds) {
        List<Notification> notifications = new ArrayList<>();
        
        for (String studentId : studentIds) {
            Notification notification = new Notification();
            notification.setUserId(studentId);
            notification.setTitle("新任务提醒");
            notification.setMessage(String.format("老师发布了新的任务：%s", taskTitle));
            notification.setType(NotificationType.TASK);
            notification.setReadFlag(false);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setUpdatedAt(LocalDateTime.now());
            notification.setRelatedId(taskId);
            notification.setRelatedType("TASK");
            notification.setCourseId(courseId);
            notification.setClassId(classId);
            notification.setPriority("normal");
            notification.setStatus("active");
            
            notifications.add(notification);
        }
        
        if (!notifications.isEmpty()) {
            notificationService.createBatchNotifications(notifications);
        }
    }

    /**
     * 创建DDL提醒通知
     */
    public void createDDLReminderNotification(Long taskId, String taskTitle, Long courseId, Long classId, List<String> studentIds, String deadline) {
        List<Notification> notifications = new ArrayList<>();
        
        for (String studentId : studentIds) {
            Notification notification = new Notification();
            notification.setUserId(studentId);
            notification.setTitle("DDL提醒");
            notification.setMessage(String.format("任务「%s」即将截止，截止时间：%s", taskTitle, deadline));
            notification.setType(NotificationType.DDL_REMINDER);
            notification.setReadFlag(false);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setUpdatedAt(LocalDateTime.now());
            notification.setRelatedId(taskId);
            notification.setRelatedType("TASK");
            notification.setCourseId(courseId);
            notification.setClassId(classId);
            notification.setPriority("high");
            notification.setStatus("active");
            
            notifications.add(notification);
        }
        
        if (!notifications.isEmpty()) {
            notificationService.createBatchNotifications(notifications);
        }
    }

    /**
     * 创建资源上传通知
     */
    public void createResourceUploadNotification(Long resourceId, String resourceTitle, Long courseId, Long classId, List<String> studentIds) {
        List<Notification> notifications = new ArrayList<>();
        
        for (String studentId : studentIds) {
            Notification notification = new Notification();
            notification.setUserId(studentId);
            notification.setTitle("新资源通知");
            notification.setMessage(String.format("老师上传了新的教学资源：%s", resourceTitle));
            notification.setType(NotificationType.RESOURCE_UPLOAD);
            notification.setReadFlag(false);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setUpdatedAt(LocalDateTime.now());
            notification.setRelatedId(resourceId);
            notification.setRelatedType("TEACHING_RESOURCE");
            notification.setCourseId(courseId);
            notification.setClassId(classId);
            notification.setPriority("normal");
            notification.setStatus("active");
            
            notifications.add(notification);
        }
        
        if (!notifications.isEmpty()) {
            notificationService.createBatchNotifications(notifications);
        }
    }

    /**
     * 创建讨论回复通知
     */
    public void createDiscussionReplyNotification(Long discussionId, String discussionTitle, Long courseId, Long classId, String replyUserId, String replyUserName) {
        Notification notification = new Notification();
        notification.setUserId(replyUserId);
        notification.setTitle("讨论回复通知");
        notification.setMessage(String.format("有人回复了你的讨论「%s」", discussionTitle));
        notification.setType(NotificationType.DISCUSSION_REPLY);
        notification.setReadFlag(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        notification.setRelatedId(discussionId);
        notification.setRelatedType("DISCUSSION");
        notification.setCourseId(courseId);
        notification.setClassId(classId);
        notification.setPriority("normal");
        notification.setStatus("active");
        
        notificationService.createNotification(notification);
    }

    /**
     * 创建课程通知
     */
    public void createCourseNotification(String title, String message, Long courseId, Long classId, List<String> studentIds) {
        List<Notification> notifications = new ArrayList<>();
        
        for (String studentId : studentIds) {
            Notification notification = new Notification();
            notification.setUserId(studentId);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setType(NotificationType.COURSE_NOTICE);
            notification.setReadFlag(false);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setUpdatedAt(LocalDateTime.now());
            notification.setCourseId(courseId);
            notification.setClassId(classId);
            notification.setPriority("normal");
            notification.setStatus("active");
            notification.setRelatedType("COURSE");
            
            notifications.add(notification);
        }
        
        if (!notifications.isEmpty()) {
            notificationService.createBatchNotifications(notifications);
        }
    }

    /**
     * 创建系统通知
     */
    public void createSystemNotification(String title, String message, List<String> userIds) {
        notificationService.sendSystemNotification(title, message, userIds);
    }

    /**
     * 创建成绩发布通知
     */
    public void createGradePublishNotification(Long courseId, Long classId, List<String> studentIds) {
        List<Notification> notifications = new ArrayList<>();
        
        for (String studentId : studentIds) {
            Notification notification = new Notification();
            notification.setUserId(studentId);
            notification.setTitle("成绩发布通知");
            notification.setMessage("老师已发布本课程的成绩，请及时查看");
            notification.setType(NotificationType.GRADE_PUBLISH);
            notification.setReadFlag(false);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setUpdatedAt(LocalDateTime.now());
            notification.setCourseId(courseId);
            notification.setClassId(classId);
            notification.setPriority("high");
            notification.setStatus("active");
            notification.setRelatedType("GRADE");
            
            notifications.add(notification);
        }
        
        if (!notifications.isEmpty()) {
            notificationService.createBatchNotifications(notifications);
        }
    }
}

