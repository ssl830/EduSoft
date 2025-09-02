package org.example.edusoft.content.service.notification.impl;

import org.example.edusoft.content.entity.notification.TaskReminder;
import org.example.edusoft.content.mapper.notification.TaskReminderMapper;
import org.example.edusoft.content.service.notification.TaskReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class TaskReminderServiceImpl implements TaskReminderService {
    
    @Autowired
    private TaskReminderMapper taskReminderMapper;
    
    @Override
    public TaskReminder createTaskReminder(TaskReminder taskReminder) {
        if (taskReminder.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (taskReminder.getTitle() == null || taskReminder.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("任务标题不能为空");
        }
        
        taskReminderMapper.insert(taskReminder);
        return taskReminder;
    }
    
    @Override
    public TaskReminder updateTaskReminder(TaskReminder taskReminder) {
        if (taskReminder.getId() == null) {
            throw new IllegalArgumentException("任务ID不能为空");
        }
        
        TaskReminder existing = taskReminderMapper.selectById(taskReminder.getId());
        if (existing == null) {
            throw new IllegalArgumentException("任务提醒不存在");
        }
        
        taskReminderMapper.update(taskReminder);
        return taskReminder;
    }
    
    @Override
    public void deleteTaskReminder(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("任务ID不能为空");
        }
        
        TaskReminder existing = taskReminderMapper.selectById(id);
        if (existing == null) {
            throw new IllegalArgumentException("任务提醒不存在");
        }
        
        taskReminderMapper.deleteById(id);
    }
    
    @Override
    public TaskReminder getTaskReminderById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("任务ID不能为空");
        }
        
        return taskReminderMapper.selectById(id);
    }
    
    @Override
    public List<TaskReminder> getTaskRemindersByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        return taskReminderMapper.selectByUserId(userId);
    }
    
    @Override
    public List<TaskReminder> getUncompletedTaskReminders(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        return taskReminderMapper.selectUncompletedByUserId(userId);
    }
    
    @Override
    public List<TaskReminder> getCompletedTaskReminders(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        return taskReminderMapper.selectCompletedByUserId(userId);
    }
    
    @Override
    public void markTaskAsCompleted(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("任务ID不能为空");
        }
        
        TaskReminder existing = taskReminderMapper.selectById(id);
        if (existing == null) {
            throw new IllegalArgumentException("任务提醒不存在");
        }
        
        taskReminderMapper.markAsCompleted(id);
    }
    
    @Override
    public void markTaskAsUncompleted(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("任务ID不能为空");
        }
        
        TaskReminder existing = taskReminderMapper.selectById(id);
        if (existing == null) {
            throw new IllegalArgumentException("任务提醒不存在");
        }
        
        taskReminderMapper.markAsUncompleted(id);
    }
}
