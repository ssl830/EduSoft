package org.example.edusoft.content.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LearningProgress {
    private Long id;
    private Long resourceId;
    private Long studentId;
    private Double progress;
    private Integer lastPosition;
    private Integer watchCount;
    private LocalDateTime lastWatchTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

