package org.example.edusoft.content.service.notification;

import org.example.edusoft.content.entity.notification.TaskReminder;
import java.util.List;

public interface TaskReminderService {
    TaskReminder createTaskReminder(TaskReminder taskReminder);
    
    TaskReminder updateTaskReminder(TaskReminder taskReminder);
    
    void deleteTaskReminder(Long id);
    
    TaskReminder getTaskReminderById(Long id);
    
    List<TaskReminder> getTaskRemindersByUserId(String userId);
    
    List<TaskReminder> getUncompletedTaskReminders(String userId);
    
    List<TaskReminder> getCompletedTaskReminders(String userId);
    
    void markTaskAsCompleted(Long id);
    
    void markTaskAsUncompleted(Long id);
} 