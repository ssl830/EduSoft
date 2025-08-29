package org.example.edusoft.learning.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PracticeDTO {
    private Long id;
    private Long courseId;
    private Long classId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean allowMultipleSubmission;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Boolean isCompleted;
    private Integer submissionCount;
    private Integer score;
}
