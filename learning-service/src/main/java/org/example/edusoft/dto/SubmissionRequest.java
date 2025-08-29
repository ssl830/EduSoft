package org.example.edusoft.learning.dto.practice;

import lombok.Data;
import java.util.List;

@Data
public class SubmissionRequest {
    private Long practiceId;      // 练习ID
    private Long studentId;       // 学生ID
    private List<String> answers; // 答案列表，按题目顺序排列
}
