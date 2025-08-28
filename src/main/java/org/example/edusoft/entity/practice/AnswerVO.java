package org.example.edusoft.entity.practice;

import lombok.Data;

// AnswerVO.java（返回给前端的答案详情）
@Data
public class AnswerVO {
    private Long id;
    private String type;
    private String content;
    private String studentAnswer;
    private String correctAnswer;
    private Integer score;
    private Boolean isJudged;
}
