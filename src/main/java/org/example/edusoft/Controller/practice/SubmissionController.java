package org.example.edusoft.controller.practice;

import java.util.List;
import org.example.edusoft.common.domain.Result;
import org.example.edusoft.dto.practice.SubmissionRequest;
import org.example.edusoft.service.practice.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 练习提交控制器
 * 处理学生提交练习答案的请求
 */
@RestController
@RequestMapping("/api/submission")
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    /**
     * 提交练习答案
     * @param request 包含练习ID、学生ID和答案列表的请求
     * @return 提交ID
     */
    @PostMapping("/submit")
    public Result<Long> submitPractice(@RequestBody SubmissionRequest request) {
        return submissionService.submitAndAutoJudge(
            request.getPracticeId(),
            request.getStudentId(),
            request.getAnswers()
        );
    }
}