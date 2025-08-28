package org.example.edusoft.dto.practice;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 待批改的提交信息DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingSubmissionDTO {
    private String studentName;    // 学生姓名
    private String practiceName;   // 练习名称
    private Long submissionId;     // 提交ID
}
