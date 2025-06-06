package org.example.edusoft.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Question {
    private Long id;
    private Long creatorId;
    private QuestionType type;
    private String content;
    private String options; // JSON格式存储选项
    private String answer;
    private LocalDateTime createdAt;

    public enum QuestionType {
        choice,
        program
    }
} 