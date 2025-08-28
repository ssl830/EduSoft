package org.example.edusoft.dto.practice;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
 
/**
 * 题目评分请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JudgeQuestionRequest {
    private String answerText;     // 答案内容
    private Integer score;         // 评分
    private Integer maxScore;      // 满分
    private Long sortOrder;        // 题目顺序
} 