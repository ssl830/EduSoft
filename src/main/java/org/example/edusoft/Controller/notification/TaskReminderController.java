package org.example.edusoft.controller.notification;

import org.example.edusoft.common.Result;
import org.example.edusoft.entity.notification.TaskReminder;
import org.example.edusoft.service.notification.TaskReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/task-reminders")
@Validated
public class TaskReminderController {

    @Autowired
    private TaskReminderService taskReminderService;

    @PostMapping
    public Result<TaskReminder> createTaskReminder(@Valid @RequestBody TaskReminder taskReminder) {
        return Result.success(taskReminderService.createTaskReminder(taskReminder));
    }

    @PutMapping("/{id}")
    public Result<TaskReminder> updateTaskReminder(@NotNull(message = "任务ID不能为空") @PathVariable Long id,
                                                 @Valid @RequestBody TaskReminder taskReminder) {
        taskReminder.setId(id);
        return Result.success(taskReminderService.updateTaskReminder(taskReminder));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteTaskReminder(@NotNull(message = "任务ID不能为空") @PathVariable Long id) {
        taskReminderService.deleteTaskReminder(id);
        return Result.success(true);
    }

    @GetMapping("/{id}")
    public Result<TaskReminder> getTaskReminder(@NotNull(message = "任务ID不能为空") @PathVariable Long id) {
        return Result.success(taskReminderService.getTaskReminderById(id));
    }

    @GetMapping("/user/{userId}")
    public Result<List<TaskReminder>> getUserTaskReminders(@NotNull(message = "用户ID不能为空") @PathVariable Long userId) {
        return Result.success(taskReminderService.getTaskRemindersByUserId(userId));
    }

    @GetMapping("/user/{userId}/uncompleted")
    public Result<List<TaskReminder>> getUncompletedTaskReminders(@NotNull(message = "用户ID不能为空") @PathVariable Long userId) {
        return Result.success(taskReminderService.getUncompletedTaskReminders(userId));
    }

    @GetMapping("/user/{userId}/completed")
    public Result<List<TaskReminder>> getCompletedTaskReminders(@NotNull(message = "用户ID不能为空") @PathVariable Long userId) {
        return Result.success(taskReminderService.getCompletedTaskReminders(userId));
    }

    @PutMapping("/{id}/complete")
    public Result<Boolean> markTaskAsCompleted(@NotNull(message = "任务ID不能为空") @PathVariable Long id) {
        taskReminderService.markTaskAsCompleted(id);
        return Result.success(true);
    }

    @PutMapping("/{id}/uncomplete")
    public Result<Boolean> markTaskAsUncompleted(@NotNull(message = "任务ID不能为空") @PathVariable Long id) {
        taskReminderService.markTaskAsUncompleted(id);
        return Result.success(true);
    }
} 