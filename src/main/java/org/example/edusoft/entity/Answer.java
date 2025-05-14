package org.example.edusoft.entity;

import lombok.Data;

@Data
public class Answer {
    private Long id;
    private Long submissionId;
    private Long questionId;
    private String answerText;
    private Boolean correct;
    private Integer score;
} 