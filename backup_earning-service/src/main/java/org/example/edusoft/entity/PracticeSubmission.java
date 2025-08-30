package org.example.edusoft.learning.entity.practice;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PracticeSubmission {
    private Long id;
    private Long practiceId;
    private Long studentId;
    private Integer score;
    private Integer isJudged;
    private LocalDateTime submittedAt;
}
