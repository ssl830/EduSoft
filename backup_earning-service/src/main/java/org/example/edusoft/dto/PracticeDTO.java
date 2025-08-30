package org.example.edusoft.learning.dto.practice;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PracticeDTO {
    private Long id;
    private String title;
    private String description;
    private Long courseId;
    private Long classId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean allowMultipleSubmission;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
