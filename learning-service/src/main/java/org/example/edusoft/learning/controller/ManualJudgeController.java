package org.example.edusoft.learning.controller;

import lombok.RequiredArgsConstructor;
import org.example.edusoft.learning.dto.SubmissionDetailRequest;
import org.example.edusoft.learning.Result;
import org.example.edusoft.learning.dto.JudgeSubmissionRequest;
import org.example.edusoft.learning.dto.PendingListRequest;
import org.example.edusoft.learning.dto.PendingSubmissionDTO;
import org.example.edusoft.learning.dto.SubmissionDetailDTO;
import org.example.edusoft.learning.service.ManualJudgeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/judge")
@RequiredArgsConstructor
public class ManualJudgeController {
 
    private final ManualJudgeService manualJudgeService;

    /**
     * 获取待批改的提交列表
     * @param request 包含练习ID（可选）和班级ID的请求
     * @return 待批改的提交列表
     */
    @PostMapping("/pendinglist")
    public Result<List<PendingSubmissionDTO>> getPendingList(
            @RequestBody PendingListRequest request) {
        System.out.println("controller called");
        return manualJudgeService.getPendingSubmissionList(request.getPracticeId(), request.getClassId());
    }

    /**
     * 获取指定提交的答案详情
     * @param request submissionId 提交ID
     * @return 答案详情列表
     */
    @PostMapping("/pending")
    public Result<List<SubmissionDetailDTO>> getSubmissionDetail(
            @RequestBody SubmissionDetailRequest request) {
        return manualJudgeService.getSubmissionDetail(request.getSubmissionId());
    }

    /**
     * 批改练习
     * @param request 批改请求，包含提交ID和评分信息
     * @return 批改结果
     */
    @PostMapping("/judge")
    public Result<Void> judgeAnswers(@RequestBody JudgeSubmissionRequest request) {
        return manualJudgeService.judgeSubmission(request);
    }
}
