package org.example.edusoft.content.controller.notification;

import org.example.edusoft.content.common.Result;
import org.example.edusoft.content.entity.notification.Notification;
import org.example.edusoft.content.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
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
    public Result<List<Notification>> getUserNotifications(HttpServletRequest request) {
        try {
            // 从请求属性中获取用户ID（由TokenInterceptor设置，现在是String类型）
            String userId = (String) request.getAttribute("userId");
            if (userId == null) {
                return Result.error(400, "无法获取用户ID，请检查认证token");
            }
            
            List<Notification> notifications = notificationService.getUserNotifications(userId);
            return Result.success(notifications);
        } catch (Exception e) {
            return Result.error(500, "获取通知列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户的分页通知
     */
    @GetMapping("/paged")
    public Result<List<Notification>> getUserNotificationsPaged(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            String userId = (String) request.getAttribute("userId");
            if (userId == null) {
                return Result.error(400, "无法获取用户ID，请检查认证token");
            }
            
            List<Notification> notifications = notificationService.getUserNotificationsPaged(userId, page, size);
            return Result.success(notifications);
        } catch (Exception e) {
            return Result.error(500, "获取分页通知失败：" + e.getMessage());
        }
    }

    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread")
    public Result<Map<String, Integer>> getUnreadCount(HttpServletRequest request) {
        try {
            // 添加调试日志
            System.out.println("=== DEBUG: getUnreadCount 开始 ===");
            
            Object userIdAttr = request.getAttribute("userId");
            System.out.println("DEBUG: request.getAttribute('userId') = " + userIdAttr);
            System.out.println("DEBUG: userIdAttr type = " + (userIdAttr != null ? userIdAttr.getClass().getName() : "null"));
            
            String userId = (String) request.getAttribute("userId");
            if (userId == null) {
                System.out.println("DEBUG: userId 为 null，返回错误");
                return Result.error(400, "无法获取用户ID，请检查认证token");
            }
            
            System.out.println("DEBUG: 最终使用的 userId = " + userId);
            int count = notificationService.getUnreadCount(userId);
            System.out.println("DEBUG: notificationService.getUnreadCount 返回: " + count);
            
            System.out.println("=== DEBUG: getUnreadCount 结束 ===");
            return Result.success(Map.of("count", count));
        } catch (Exception e) {
            System.out.println("DEBUG: 发生异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error(500, "获取未读通知数量失败：" + e.getMessage());
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
            return Result.error(500, "标记通知为已读失败：" + e.getMessage());
        }
    }

    /**
     * 标记所有通知为已读
     */
    @PutMapping("/read-all")
    public Result<Void> markAllAsRead(HttpServletRequest request) {
        try {
            String userId = (String) request.getAttribute("userId");
            if (userId == null) {
                return Result.error(400, "无法获取用户ID，请检查认证token");
            }
            
            notificationService.markAllAsRead(userId);
            return Result.success(null, "所有通知已标记为已读");
        } catch (Exception e) {
            return Result.error(500, "标记所有通知为已读失败：" + e.getMessage());
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
            return Result.error(500, "删除通知失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取通知
     */
    @GetMapping("/{id}")
    public Result<Notification> getNotificationById(@PathVariable Long id) {
        try {
            Notification notification = notificationService.getNotificationById(id);
            return Result.success(notification);
        } catch (Exception e) {
            return Result.error(500, "获取通知失败：" + e.getMessage());
        }
    }

    /**
     * 按类型查询通知
     */
    @GetMapping("/type/{type}")
    public Result<List<Notification>> getNotificationsByType(
            HttpServletRequest request,
            @PathVariable String type) {
        try {
            String userId = (String) request.getAttribute("userId");
            if (userId == null) {
                return Result.error(400, "无法获取用户ID，请检查认证token");
            }
            
            List<Notification> notifications = notificationService.getUserNotificationsByType(userId, type);
            return Result.success(notifications);
        } catch (Exception e) {
            return Result.error(500, "按类型查询通知失败：" + e.getMessage());
        }
    }

    /**
     * 按课程查询通知
     */
    @GetMapping("/course/{courseId}")
    public Result<List<Notification>> getNotificationsByCourse(
            HttpServletRequest request,
            @PathVariable Long courseId) {
        try {
            String userId = (String) request.getAttribute("userId");
            if (userId == null) {
                return Result.error(400, "无法获取用户ID，请检查认证token");
            }
            
            List<Notification> notifications = notificationService.getUserNotificationsByCourse(userId, courseId);
            return Result.success(notifications);
        } catch (Exception e) {
            return Result.error(500, "按课程查询通知失败：" + e.getMessage());
        }
    }

    /**
     * 按班级查询通知
     */
    @GetMapping("/class/{classId}")
    public Result<List<Notification>> getNotificationsByClass(
            HttpServletRequest request,
            @PathVariable Long classId) {
        try {
            String userId = (String) request.getAttribute("userId");
            if (userId == null) {
                return Result.error(400, "无法获取用户ID，请检查认证token");
            }
            
            List<Notification> notifications = notificationService.getUserNotificationsByClass(userId, classId);
            return Result.success(notifications);
        } catch (Exception e) {
            return Result.error(500, "按班级查询通知失败：" + e.getMessage());
        }
    }

    /**
     * 搜索通知
     */
    @GetMapping("/search")
    public Result<List<Notification>> searchNotifications(
            HttpServletRequest request,
            @RequestParam String keyword) {
        try {
            String userId = (String) request.getAttribute("userId");
            if (userId == null) {
                return Result.error(400, "无法获取用户ID，请检查认证token");
            }
            
            List<Notification> notifications = notificationService.searchUserNotifications(userId, keyword);
            return Result.success(notifications);
        } catch (Exception e) {
            return Result.error(500, "搜索通知失败：" + e.getMessage());
        }
    }

    /**
     * 获取通知统计
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getNotificationStats(HttpServletRequest request) {
        try {
            String userId = (String) request.getAttribute("userId");
            if (userId == null) {
                return Result.error(400, "无法获取用户ID，请检查认证token");
            }
            
            Map<String, Object> stats = notificationService.getNotificationStats(userId);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error(500, "获取通知统计失败：" + e.getMessage());
        }
    }

    /**
     * 发送系统通知
     */
    @PostMapping("/system")
    public Result<Void> sendSystemNotification(@RequestBody Map<String, Object> request) {
        try {
            String title = (String) request.get("title");
            String message = (String) request.get("message");
            @SuppressWarnings("unchecked")
            List<String> userIds = (List<String>) request.get("userIds");
            
            notificationService.sendSystemNotification(title, message, userIds);
            return Result.success(null, "系统通知发送成功");
        } catch (Exception e) {
            return Result.error(500, "发送系统通知失败：" + e.getMessage());
        }
    }

    /**
     * 发送课程通知
     */
    @PostMapping("/course")
    public Result<Void> sendCourseNotification(@RequestBody Map<String, Object> request) {
        try {
            String title = (String) request.get("title");
            String message = (String) request.get("message");
            Long courseId = Long.valueOf(request.get("courseId").toString());
            @SuppressWarnings("unchecked")
            List<String> userIds = (List<String>) request.get("userIds");
            
            notificationService.sendCourseNotification(title, message, courseId, userIds);
            return Result.success(null, "课程通知发送成功");
        } catch (Exception e) {
            return Result.error(500, "发送课程通知失败：" + e.getMessage());
        }
    }

    /**
     * 发送班级通知
     */
    @PostMapping("/class")
    public Result<Void> sendClassNotification(@RequestBody Map<String, Object> request) {
        try {
            String title = (String) request.get("title");
            String message = (String) request.get("message");
            Long classId = Long.valueOf(request.get("classId").toString());
            @SuppressWarnings("unchecked")
            List<String> userIds = (List<String>) request.get("userIds");
            
            notificationService.sendClassNotification(title, message, classId, userIds);
            return Result.success(null, "班级通知发送成功");
        } catch (Exception e) {
            return Result.error(500, "发送班级通知失败：" + e.getMessage());
        }
    }

    /**
     * 清理过期通知
     */
    @DeleteMapping("/cleanup")
    public Result<Void> cleanupExpiredNotifications(@RequestParam(defaultValue = "30") int days) {
        try {
            notificationService.cleanupExpiredNotifications(days);
            return Result.success(null, "过期通知清理成功");
        } catch (Exception e) {
            return Result.error(500, "清理过期通知失败：" + e.getMessage());
        }
    }
} 