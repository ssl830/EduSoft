package org.example.edusoft.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Progress {
    private Long id;
    private Long studentId;
    private Long courseId;
    private Long sectionId;
    private Boolean completed;
    private LocalDateTime completedAt;
} 