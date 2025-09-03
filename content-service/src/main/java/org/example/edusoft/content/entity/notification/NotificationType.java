package org.example.edusoft.content.entity.notification;

/**
 * 通知类型枚举
 * 定义了系统中所有可能的通知类型：
 * - COURSE_NOTICE: 课程通知，用于发布课程相关的通知
 * - PRACTICE_NOTICE: 练习通知，当老师发布新练习时
 * - DDL_REMINDER: DDL提醒，在任务截止时间前3天发送
 * - TASK_REMINDER: 任务提醒，当老师发布新任务时
 * - HOMEWORK: 作业通知，当老师发布新作业时
 * - TASK: 任务通知，用于任务相关的通知
 * - PRACTICE: 在线练习通知，用于练习相关的通知
 * - SYSTEM: 系统通知，用于系统级别的通知
 * - ASSIGNMENT: 作业分配，用于作业分配相关的通知
 * - DISCUSSION_REPLY: 讨论回复，当有人回复讨论时
 * - RESOURCE_UPLOAD: 资源上传通知
 * - COURSE_ENROLLMENT: 课程报名通知 
 * - GRADE_PUBLISH: 成绩发布通知
 * - DEADLINE_WARNING: 截止时间警告
 * - COURSE_SCHEDULE: 课程安排变更
 * - ANNOUNCEMENT: 公告通知
 */
public enum NotificationType {
    COURSE_NOTICE("课程通知"),
    PRACTICE_NOTICE("练习通知"),
    DDL_REMINDER("DDL提醒"),
    TASK_REMINDER("任务提醒"),
    HOMEWORK("作业"),
    TASK("任务"),
    PRACTICE("在线练习"),
    SYSTEM("系统通知"),
    ASSIGNMENT("作业分配"),
    DISCUSSION_REPLY("讨论回复"),
    RESOURCE_UPLOAD("资源上传"),
    COURSE_ENROLLMENT("课程报名"),
    GRADE_PUBLISH("成绩发布"),
    DEADLINE_WARNING("截止时间警告"),
    COURSE_SCHEDULE("课程安排变更"),
    ANNOUNCEMENT("公告通知");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}