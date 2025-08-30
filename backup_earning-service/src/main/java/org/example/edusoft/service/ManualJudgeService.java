package org.example.edusoft.learning.service;

import java.util.List;
import org.example.edusoft.common.domain.Result;
import org.example.edusoft.learning.dto.practice.JudgeSubmissionRequest;
import org.example.edusoft.learning.dto.practice.PendingSubmissionDTO;
import org.example.edusoft.learning.dto.practice.SubmissionDetailDTO;

/**
 * 教师批改服务
 */
public interface ManualJudgeService {

    /**
     * 获取待批改的提交列表
     */
    Result<List<PendingSubmissionDTO>> getPendingSubmissionList(Long practiceId, Long classId);

    /**
     * 获取提交答案详情
     */
    Result<List<SubmissionDetailDTO>> getSubmissionDetail(Long submissionId);

    /**
     * 批改提交
     */
    Result<Void> judgeSubmission(JudgeSubmissionRequest request);
}
