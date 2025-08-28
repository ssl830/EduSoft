package org.example.edusoft.service.notification.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.edusoft.common.exception.NotificationException;
import org.example.edusoft.entity.notification.TaskReminder;
import org.example.edusoft.mapper.notification.TaskReminderMapper;
import org.example.edusoft.service.notification.TaskReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class TaskReminderServiceImpl implements TaskReminderService {

    private static final List<String> VALID_PRIORITIES = Arrays.asList("HIGH", "MEDIUM", "LOW");

    @Autowired
    private TaskReminderMapper taskReminderMapper;

    @Override
    @Transactional
    public TaskReminder createTaskReminder(TaskReminder taskReminder) {
        // 验证必填字段
        if (taskReminder.getTitle() == null || taskReminder.getTitle().trim().isEmpty()) {
            throw new NotificationException("任务标题不能为空");
        }
        if (taskReminder.getDeadline() == null) {
            throw new NotificationException("截止日期不能为空");
        }
        if (taskReminder.getPriority() == null) {
            throw new NotificationException("优先级不能为空");
        }
        
        // 验证优先级
        if (!VALID_PRIORITIES.contains(taskReminder.getPriority())) {
            throw NotificationException.invalidTaskPriority();
        }
        
        // 验证截止时间
        if (taskReminder.getDeadline().isBefore(LocalDateTime.now())) {
            throw NotificationException.invalidDeadline();
        }
        
        taskReminder.setCreateTime(LocalDateTime.now());
        taskReminder.setCompleted(false);
        taskReminderMapper.insert(taskReminder);
        return taskReminder;
    }

    @Override
    @Transactional
    public TaskReminder updateTaskReminder(TaskReminder taskReminder) {
        if (taskReminder.getId() == null) {
            throw new NotificationException("任务ID不能为空");
        }
        
        TaskReminder existingTask = taskReminderMapper.selectById(taskReminder.getId());
        if (existingTask == null) {
            throw NotificationException.taskReminderNotFound();
        }
        
        // 验证优先级
        if (taskReminder.getPriority() != null && !VALID_PRIORITIES.contains(taskReminder.getPriority())) {
            throw NotificationException.invalidTaskPriority();
        }
        
        // 验证截止时间
        if (taskReminder.getDeadline() != null && taskReminder.getDeadline().isBefore(LocalDateTime.now())) {
            throw NotificationException.invalidDeadline();
        }
        
        taskReminderMapper.updateById(taskReminder);
        return taskReminder;
    }

    @Override
    @Transactional
    public void deleteTaskReminder(Long id) {
        if (id == null) {
            throw new NotificationException("任务ID不能为空");
        }
        if (taskReminderMapper.selectById(id) == null) {
            throw NotificationException.taskReminderNotFound();
        }
        taskReminderMapper.deleteById(id);
    }

    @Override
    public TaskReminder getTaskReminderById(Long id) {
        if (id == null) {
            throw new NotificationException("任务ID不能为空");
        }
        TaskReminder taskReminder = taskReminderMapper.selectById(id);
        if (taskReminder == null) {
            throw NotificationException.taskReminderNotFound();
        }
        return taskReminder;
    }

    @Override
    public List<TaskReminder> getTaskRemindersByUserId(Long userId) {
        if (userId == null) {
            throw new NotificationException("用户ID不能为空");
        }
        return taskReminderMapper.getTaskRemindersByUserId(userId);
    }

    @Override
    public List<TaskReminder> getUncompletedTaskReminders(Long userId) {
        if (userId == null) {
            throw new NotificationException("用户ID不能为空");
        }
        return taskReminderMapper.getUncompletedTaskReminders(userId);
    }

    @Override
    public List<TaskReminder> getCompletedTaskReminders(Long userId) {
        if (userId == null) {
            throw new NotificationException("用户ID不能为空");
        }
        return taskReminderMapper.getCompletedTaskReminders(userId);
    }

    @Override
    @Transactional
    public void markTaskAsCompleted(Long id) {
        if (id == null) {
            throw new NotificationException("任务ID不能为空");
        }
        TaskReminder taskReminder = taskReminderMapper.selectById(id);
        if (taskReminder == null) {
            throw NotificationException.taskReminderNotFound();
        }
        
        if (taskReminder.getCompleted()) {
            throw NotificationException.taskAlreadyCompleted();
        }
        
        taskReminder.setCompleted(true);
        taskReminder.setCompletedTime(LocalDateTime.now());
        taskReminderMapper.updateById(taskReminder);
    }

    @Override
    @Transactional
    public void markTaskAsUncompleted(Long id) {
        if (id == null) {
            throw new NotificationException("任务ID不能为空");
        }
        TaskReminder taskReminder = taskReminderMapper.selectById(id);
        if (taskReminder == null) {
            throw NotificationException.taskReminderNotFound();
        }
        
        if (!taskReminder.getCompleted()) {
            throw NotificationException.taskNotCompleted();
        }
        
        taskReminder.setCompleted(false);
        taskReminder.setCompletedTime(null);
        taskReminderMapper.updateById(taskReminder);
    }
} 