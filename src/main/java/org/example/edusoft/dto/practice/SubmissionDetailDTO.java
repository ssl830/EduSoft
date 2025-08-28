package org.example.edusoft.dto.practice;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 提交答案详情DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionDetailDTO {
    private String questionName;   // 题目内容
    private String answerText;     // 答案内容
    private Integer maxScore;      // 题目满分
    private String studentName;    // 学生姓名
    private Long sortOrder;        // 题目顺序
}
