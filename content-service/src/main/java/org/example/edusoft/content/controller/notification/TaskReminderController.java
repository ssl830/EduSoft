package org.example.edusoft.content.controller.notification;

import org.example.edusoft.content.common.Result;
import org.example.edusoft.content.entity.notification.TaskReminder;
import org.example.edusoft.content.service.notification.TaskReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
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
    public Result<TaskReminder> createTaskReminder(@Valid @RequestBody TaskReminder taskReminder, HttpServletRequest request) {
        try {
            // 从请求属性中获取用户ID（由TokenInterceptor设置，现在是String类型）
            String userId = (String) request.getAttribute("userId");
            if (userId == null) {
                return Result.error(400, "无法获取用户ID，请检查认证token");
            }
            
            taskReminder.setUserId(userId);
            
            return Result.success(taskReminderService.createTaskReminder(taskReminder));
        } catch (Exception e) {
            return Result.error(500, "创建任务提醒失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<TaskReminder> updateTaskReminder(@NotNull(message = "任务ID不能为空") @PathVariable Long id,
                                                 @Valid @RequestBody TaskReminder taskReminder) {
        try {
            taskReminder.setId(id);
            return Result.success(taskReminderService.updateTaskReminder(taskReminder));
        } catch (Exception e) {
            return Result.error(500, "更新任务提醒失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteTaskReminder(@NotNull(message = "任务ID不能为空") @PathVariable Long id) {
        try {
            taskReminderService.deleteTaskReminder(id);
            return Result.success(true);
        } catch (Exception e) {
            return Result.error(500, "删除任务提醒失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<TaskReminder> getTaskReminder(@NotNull(message = "任务ID不能为空") @PathVariable Long id) {
        try {
            return Result.success(taskReminderService.getTaskReminderById(id));
        } catch (Exception e) {
            return Result.error(500, "获取任务提醒失败：" + e.getMessage());
        }
    }

    @GetMapping("/user")
    public Result<List<TaskReminder>> getUserTaskReminders(HttpServletRequest request) {
        try {
            String userId = (String) request.getAttribute("userId");
            if (userId == null) {
                return Result.error(400, "无法获取用户ID，请检查认证token");
            }
            
            return Result.success(taskReminderService.getTaskRemindersByUserId(userId));
        } catch (Exception e) {
            return Result.error(500, "获取用户任务提醒失败：" + e.getMessage());
        }
    }

    @GetMapping("/user/uncompleted")
    public Result<List<TaskReminder>> getUncompletedTaskReminders(HttpServletRequest request) {
        try {
            String userId = (String) request.getAttribute("userId");
            if (userId == null) {
                return Result.error(400, "无法获取用户ID，请检查认证token");
            }
            
            return Result.success(taskReminderService.getUncompletedTaskReminders(userId));
        } catch (Exception e) {
            return Result.error(500, "获取未完成任务提醒失败：" + e.getMessage());
        }
    }

    @GetMapping("/user/completed")
    public Result<List<TaskReminder>> getCompletedTaskReminders(HttpServletRequest request) {
        try {
            String userId = (String) request.getAttribute("userId");
            if (userId == null) {
                return Result.error(400, "无法获取用户ID，请检查认证token");
            }
            
            return Result.success(taskReminderService.getCompletedTaskReminders(userId));
        } catch (Exception e) {
            return Result.error(500, "获取已完成任务提醒失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}/complete")
    public Result<Boolean> markTaskAsCompleted(@NotNull(message = "任务ID不能为空") @PathVariable Long id) {
        try {
            taskReminderService.markTaskAsCompleted(id);
            return Result.success(true);
        } catch (Exception e) {
            return Result.error(500, "标记任务完成失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}/uncomplete")
    public Result<Boolean> markTaskAsUncompleted(@NotNull(message = "任务ID不能为空") @PathVariable Long id) {
        try {
            taskReminderService.markTaskAsUncompleted(id);
            return Result.success(true);
        } catch (Exception e) {
            return Result.error(500, "标记任务未完成失败：" + e.getMessage());
        }
    }
} 