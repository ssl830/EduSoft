package org.example.edusoft.learning.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JudgeQuestionRequest {
    private String answerText;
    private Integer score;
    private Integer maxScore;
    private Long sortOrder;
}
