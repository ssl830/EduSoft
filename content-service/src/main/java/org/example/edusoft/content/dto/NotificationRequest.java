package org.example.edusoft.content.dto;

import lombok.Data;

@Data
public class NotificationRequest {
    private Long userId;
    private String title;
    private String message;
    private String type;
    private Long relatedId;
    private String relatedType;
    private String priority;
}
