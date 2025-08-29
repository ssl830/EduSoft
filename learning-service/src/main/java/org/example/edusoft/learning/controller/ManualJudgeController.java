package org.example.edusoft.learning.controller;

import lombok.RequiredArgsConstructor;
import org.example.edusoft.learning.Result;
import org.example.edusoft.learning.dto.JudgeSubmissionRequest;
import org.example.edusoft.learning.dto.PendingListRequest;
import org.example.edusoft.learning.dto.PendingSubmissionDTO;
import org.example.edusoft.learning.dto.SubmissionDetailDTO;
import org.example.edusoft.learning.service.ManualJudgeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning/judge")
@RequiredArgsConstructor
public class ManualJudgeController {
 
    private final ManualJudgeService manualJudgeService;

    @PostMapping("/pending-list")
    public Result<List<PendingSubmissionDTO>> getPendingList(
            @RequestBody PendingListRequest request) {
        return manualJudgeService.getPendingSubmissionList(request.getPracticeId(), request.getClassId());
    }

    @GetMapping("/submission/{submissionId}")
    public Result<List<SubmissionDetailDTO>> getSubmissionDetail(
            @PathVariable Long submissionId) {
        return manualJudgeService.getSubmissionDetail(submissionId);
    }

    @PostMapping("/submission")
    public Result<Void> judgeAnswers(@RequestBody JudgeSubmissionRequest request) {
        return manualJudgeService.judgeSubmission(request);
    }
}
