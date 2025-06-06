package org.example.edusoft.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Notification {
    private Long id;
    private Long userId;
    private String title;
    private String message;
    private Boolean readFlag;
    private LocalDateTime createdAt;
} 