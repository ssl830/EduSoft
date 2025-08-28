package org.example.edusoft.dto.practice;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 * 批改提交请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JudgeSubmissionRequest {
    private Long submissionId;                 // 提交ID
    private List<JudgeQuestionRequest> questions;  // 题目评分列表
}
