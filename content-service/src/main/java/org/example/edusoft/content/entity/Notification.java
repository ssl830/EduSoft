package org.example.edusoft.content.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Notification {
    private Long id;
    private Long userId;
    private String title;
    private String message;
    private String type;
    private Boolean readFlag;
    private Long relatedId;
    private String relatedType;
    private String priority; // low, normal, high, urgent
    private String status; // active, expired, deleted
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
