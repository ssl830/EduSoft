package org.example.edusoft.content.controller;

import org.example.edusoft.content.common.Result;
import org.example.edusoft.content.entity.notification.Notification;
import org.example.edusoft.content.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 通知控制器
 */
@RestController
@RequestMapping("/api/content/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 获取用户的所有通知
     */
    @GetMapping
    public Result<List<Notification>> getUserNotifications(@RequestAttribute Long userId) {
        try {
            List<Notification> notifications = notificationService.getUserNotifications(userId);
            return Result.success(notifications, "获取通知列表成功");
        } catch (Exception e) {
            return Result.error("获取通知列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread")
    public Result<Map<String, Integer>> getUnreadCount(@RequestAttribute Long userId) {
        try {
            int count = notificationService.getUnreadCount(userId);
            return Result.success(Map.of("count", count), "获取未读通知数量成功");
        } catch (Exception e) {
            return Result.error("获取未读通知数量失败：" + e.getMessage());
        }
    }

    /**
     * 标记通知为已读
     */
    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        try {
            notificationService.markAsRead(id);
            return Result.success(null, "通知已标记为已读");
        } catch (Exception e) {
            return Result.error("标记通知为已读失败：" + e.getMessage());
        }
    }

    /**
     * 标记所有通知为已读
     */
    @PutMapping("/read-all")
    public Result<Void> markAllAsRead(@RequestAttribute Long userId) {
        try {
            notificationService.markAllAsRead(userId);
            return Result.success(null, "所有通知已标记为已读");
        } catch (Exception e) {
            return Result.error("标记所有通知为已读失败：" + e.getMessage());
        }
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteNotification(@PathVariable Long id) {
        try {
            notificationService.deleteNotification(id);
            return Result.success(null, "通知已删除");
        } catch (Exception e) {
            return Result.error("删除通知失败：" + e.getMessage());
        }
    }
} 