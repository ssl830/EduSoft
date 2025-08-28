// AnswerDTO.java（学生提交的答案）
package org.example.edusoft.entity.practice;

import lombok.Data;

@Data
public class AnswerDTO {
    private Long questionId;
    private String answerText;
}