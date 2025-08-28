package org.example.edusoft.dto.practice;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 获取提交详情的请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionDetailRequest {
    private Long submissionId;  // 提交ID
} 