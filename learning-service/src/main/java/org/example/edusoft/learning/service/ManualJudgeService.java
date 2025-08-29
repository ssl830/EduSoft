package org.example.edusoft.learning.service;

import org.example.edusoft.learning.Result;
import org.example.edusoft.learning.dto.JudgeSubmissionRequest;
import org.example.edusoft.learning.dto.PendingSubmissionDTO;
import org.example.edusoft.learning.dto.SubmissionDetailDTO;

import java.util.List;

public interface ManualJudgeService {
    Result<List<PendingSubmissionDTO>> getPendingSubmissionList(Long practiceId, Long classId);
    Result<List<SubmissionDetailDTO>> getSubmissionDetail(Long submissionId);
    Result<Void> judgeSubmission(JudgeSubmissionRequest request);
}
