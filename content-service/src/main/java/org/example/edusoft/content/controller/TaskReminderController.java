package org.example.edusoft.content.controller;

import org.example.edusoft.content.common.Result;
import org.example.edusoft.content.entity.notification.TaskReminder;
import org.example.edusoft.content.service.notification.TaskReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/content/task-reminders")
@Validated
public class TaskReminderController {

    @Autowired
    private TaskReminderService taskReminderService;

    @PostMapping
    public Result<TaskReminder> createTaskReminder(@Valid @RequestBody TaskReminder taskReminder) {
        try {
            TaskReminder created = taskReminderService.createTaskReminder(taskReminder);
            return Result.success(created, "任务提醒创建成功");
        } catch (Exception e) {
            return Result.error("创建任务提醒失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<TaskReminder> updateTaskReminder(@NotNull(message = "任务ID不能为空") @PathVariable Long id,
                                                 @Valid @RequestBody TaskReminder taskReminder) {
        try {
            taskReminder.setId(id);
            TaskReminder updated = taskReminderService.updateTaskReminder(taskReminder);
            return Result.success(updated, "任务提醒更新成功");
        } catch (Exception e) {
            return Result.error("更新任务提醒失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteTaskReminder(@NotNull(message = "任务ID不能为空") @PathVariable Long id) {
        try {
            taskReminderService.deleteTaskReminder(id);
            return Result.success(true, "任务提醒删除成功");
        } catch (Exception e) {
            return Result.error("删除任务提醒失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<TaskReminder> getTaskReminder(@NotNull(message = "任务ID不能为空") @PathVariable Long id) {
        try {
            TaskReminder taskReminder = taskReminderService.getTaskReminderById(id);
            if (taskReminder == null) {
                return Result.error("任务提醒不存在");
            }
            return Result.success(taskReminder, "获取任务提醒成功");
        } catch (Exception e) {
            return Result.error("获取任务提醒失败：" + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public Result<List<TaskReminder>> getUserTaskReminders(@NotNull(message = "用户ID不能为空") @PathVariable Long userId) {
        try {
            List<TaskReminder> reminders = taskReminderService.getTaskRemindersByUserId(userId);
            return Result.success(reminders, "获取用户任务提醒列表成功");
        } catch (Exception e) {
            return Result.error("获取用户任务提醒列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/uncompleted")
    public Result<List<TaskReminder>> getUncompletedTaskReminders(@NotNull(message = "用户ID不能为空") @PathVariable Long userId) {
        try {
            List<TaskReminder> reminders = taskReminderService.getUncompletedTaskReminders(userId);
            return Result.success(reminders, "获取未完成任务提醒列表成功");
        } catch (Exception e) {
            return Result.error("获取未完成任务提醒列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/completed")
    public Result<List<TaskReminder>> getCompletedTaskReminders(@NotNull(message = "用户ID不能为空") @PathVariable Long userId) {
        try {
            List<TaskReminder> reminders = taskReminderService.getCompletedTaskReminders(userId);
            return Result.success(reminders, "获取已完成任务提醒列表成功");
        } catch (Exception e) {
            return Result.error("获取已完成任务提醒列表失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}/complete")
    public Result<Boolean> markTaskAsCompleted(@NotNull(message = "任务ID不能为空") @PathVariable Long id) {
        try {
            taskReminderService.markTaskAsCompleted(id);
            return Result.success(true, "任务标记为已完成");
        } catch (Exception e) {
            return Result.error("标记任务完成失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}/uncomplete")
    public Result<Boolean> markTaskAsUncompleted(@NotNull(message = "任务ID不能为空") @PathVariable Long id) {
        try {
            taskReminderService.markTaskAsUncompleted(id);
            return Result.success(true, "任务标记为未完成");
        } catch (Exception e) {
            return Result.error("标记任务未完成失败：" + e.getMessage());
        }
    }
} 