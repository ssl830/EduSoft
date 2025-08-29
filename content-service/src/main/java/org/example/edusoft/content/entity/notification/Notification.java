package org.example.edusoft.entity.notification;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 通知实体类
 */
@Data
public class Notification {
    private Long id;
    private Long userId;
    private String title;
    private String message;
    private NotificationType type;
    private Boolean readFlag;
    private LocalDateTime createdAt;
    private Long relatedId;
    private String relatedType;
} 