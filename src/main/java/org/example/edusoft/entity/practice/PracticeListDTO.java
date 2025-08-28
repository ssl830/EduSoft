package org.example.edusoft.entity.practice;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PracticeListDTO {
    private Long id;
    private Long courseId;
    private Long classId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean allowMultipleSubmission;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Boolean isCompleted;  // 是否已完成
    private Integer submissionCount;  // 提交次数
    private Integer score;  // 最高分数
} 