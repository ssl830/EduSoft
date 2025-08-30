package org.example.edusoft.learning.controller;

import lombok.RequiredArgsConstructor;
import org.example.edusoft.learning.Result;
import org.example.edusoft.learning.dto.SubmissionDTO;
import org.example.edusoft.learning.service.SubmissionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/learning/submission")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping("/submit")
    public Result<Long> submitPractice(@RequestBody SubmissionDTO request, @RequestHeader("X-User-Id") Long studentId) {
        return submissionService.submitAndAutoJudge(
            request.getPracticeId(),
            studentId,
            request.getAnswers()
        );
    }
}
