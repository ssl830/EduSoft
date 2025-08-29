package org.example.edusoft.content.controller;

import org.example.edusoft.content.entity.Notification;
import org.example.edusoft.content.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/content/notification")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 创建通知
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createNotification(@RequestBody Notification notification) {
        try {
            Notification created = notificationService.createNotification(notification);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "通知创建成功");
            response.put("data", created);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "通知创建失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 根据ID获取通知
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getNotification(@PathVariable Long id) {
        try {
            Notification notification = notificationService.getNotificationById(id);
            Map<String, Object> response = new HashMap<>();
            if (notification != null) {
                response.put("code", 200);
                response.put("msg", "获取成功");
                response.put("data", notification);
            } else {
                response.put("code", 404);
                response.put("msg", "通知不存在");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "获取通知失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 根据用户ID获取通知列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getNotificationsByUser(@PathVariable Long userId) {
        try {
            List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "获取成功");
            response.put("data", notifications);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "获取通知列表失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取用户未读通知
     */
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<Map<String, Object>> getUnreadNotifications(@PathVariable Long userId) {
        try {
            List<Notification> notifications = notificationService.getUnreadNotifications(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "获取成功");
            response.put("data", notifications);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "获取未读通知失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 根据类型获取通知
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<Map<String, Object>> getNotificationsByType(@PathVariable String type) {
        try {
            List<Notification> notifications = notificationService.getNotificationsByType(type);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "获取成功");
            response.put("data", notifications);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "获取通知失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 根据关联对象获取通知
     */
    @GetMapping("/related")
    public ResponseEntity<Map<String, Object>> getNotificationsByRelated(
            @RequestParam String relatedType,
            @RequestParam Long relatedId) {
        try {
            List<Notification> notifications = notificationService.getNotificationsByRelated(relatedType, relatedId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "获取成功");
            response.put("data", notifications);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "获取通知失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 标记通知为已读
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Long id) {
        try {
            notificationService.markAsRead(id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "标记成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "标记失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 标记用户所有通知为已读
     */
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead(@PathVariable Long userId) {
        try {
            notificationService.markAllAsRead(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "标记成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "标记失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 过期通知
     */
    @PutMapping("/{id}/expire")
    public ResponseEntity<Map<String, Object>> expireNotification(@PathVariable Long id) {
        try {
            notificationService.expireNotification(id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "通知已过期");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "操作失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}

