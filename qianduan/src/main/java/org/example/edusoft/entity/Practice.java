package org.example.edusoft.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Practice {
    private Long id;
    private Long courseId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean allowMultipleSubmission;
    private Long createdBy;
    private LocalDateTime createdAt;
} 