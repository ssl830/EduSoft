package org.example.edusoft.service.notification;

import org.example.edusoft.entity.notification.TaskReminder;
import java.util.List;

public interface TaskReminderService {
    TaskReminder createTaskReminder(TaskReminder taskReminder);
    
    TaskReminder updateTaskReminder(TaskReminder taskReminder);
    
    void deleteTaskReminder(Long id);
    
    TaskReminder getTaskReminderById(Long id);
    
    List<TaskReminder> getTaskRemindersByUserId(Long userId);
    
    List<TaskReminder> getUncompletedTaskReminders(Long userId);
    
    List<TaskReminder> getCompletedTaskReminders(Long userId);
    
    void markTaskAsCompleted(Long id);
    
    void markTaskAsUncompleted(Long id);
} 