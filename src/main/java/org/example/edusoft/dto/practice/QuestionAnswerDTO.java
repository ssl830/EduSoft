package org.example.edusoft.dto.practice;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAnswerDTO {
    private Long questionId;
    private String answerText;
    private Boolean isJudged;
    private Boolean correct;
    private Integer score;
    private Integer maxScore;
} 