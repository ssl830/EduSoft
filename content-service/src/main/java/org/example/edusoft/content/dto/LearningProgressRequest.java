package org.example.edusoft.content.dto;

import lombok.Data;

@Data
public class LearningProgressRequest {
    private Long resourceId;
    private Long studentId;
    private Double progress;
    private Integer lastPosition;
    private Integer watchCount;
}

