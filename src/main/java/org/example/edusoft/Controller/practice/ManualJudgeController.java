package org.example.edusoft.controller.practice;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.example.edusoft.common.domain.Result;
import org.example.edusoft.dto.practice.*;
import org.example.edusoft.entity.practice.*;
import org.example.edusoft.mapper.practice.*;
import org.springframework.beans.factory.annotation.*;
import org.example.edusoft.service.practice.*;
import org.springframework.web.bind.annotation.*;

/**
 * 教师批改练习控制器
 */
@RestController
@RequestMapping("/api/judge")
public class ManualJudgeController {
 
    @Autowired
    private ManualJudgeService manualJudgeService;

    /**
     * 获取待批改的提交列表
     * @param request 包含练习ID（可选）和班级ID的请求
     * @return 待批改的提交列表
     */
    @PostMapping("/pendinglist")
    public Result<List<PendingSubmissionDTO>> getPendingList(
            @RequestBody PendingListRequest request) {
        System.out.println("request: " + request);  
        return manualJudgeService.getPendingSubmissionList(request.getPracticeId(), request.getClassId());
    }

    /**
     * 获取指定提交的答案详情
     * @param submissionId 提交ID
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