package org.example.edusoft.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Submission {
    private Long id;
    private Long practiceId;
    private Long studentId;
    private LocalDateTime submittedAt;
    private Integer score;
    private String feedback;
} 