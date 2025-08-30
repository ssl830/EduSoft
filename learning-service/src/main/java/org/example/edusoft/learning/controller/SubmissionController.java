package org.example.edusoft.learning.controller;

import lombok.RequiredArgsConstructor;
import org.example.edusoft.learning.Result;
import org.example.edusoft.learning.dto.SubmissionDTO;
import org.example.edusoft.learning.service.SubmissionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submission")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    /**
     * 提交练习答案
     * @param request 包含练习ID、学生ID和答案列表的请求
     * @return 提交ID
     */
    @PostMapping("/submit")
    public Result<Long> submitPractice(@RequestBody SubmissionDTO request) {
        return submissionService.submitAndAutoJudge(
            request.getPracticeId(),
            request.getStudentId(),
            request.getAnswers()
        );
    }
}
