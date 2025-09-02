package org.example.edusoft.content.service.notification;

import org.example.edusoft.content.entity.notification.TaskReminder;
import java.util.List;

public interface TaskReminderService {
    
    /**
     * 创建任务提醒
     */
    TaskReminder createTaskReminder(TaskReminder taskReminder);
    
    /**
     * 更新任务提醒
     */
    TaskReminder updateTaskReminder(TaskReminder taskReminder);
    
    /**
     * 删除任务提醒
     */
    void deleteTaskReminder(Long id);
    
    /**
     * 根据ID获取任务提醒
     */
    TaskReminder getTaskReminderById(Long id);
    
    /**
     * 根据用户ID获取任务提醒列表
     */
    List<TaskReminder> getTaskRemindersByUserId(Long userId);
    
    /**
     * 根据用户ID获取未完成任务提醒列表
     */
    List<TaskReminder> getUncompletedTaskReminders(Long userId);
    
    /**
     * 根据用户ID获取已完成任务提醒列表
     */
    List<TaskReminder> getCompletedTaskReminders(Long userId);
    
    /**
     * 标记任务为已完成
     */
    void markTaskAsCompleted(Long id);
    
    /**
     * 标记任务为未完成
     */
    void markTaskAsUncompleted(Long id);
}
