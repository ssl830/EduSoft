package org.example.edusoft.dto.practice;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 * 提交练习答案的请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionRequest {
    private Long practiceId;      // 练习ID
    private Long studentId;       // 学生ID
    private List<String> answers; // 答案列表，按题目顺序排列
} 