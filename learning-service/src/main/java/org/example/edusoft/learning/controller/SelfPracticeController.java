package org.example.edusoft.learning.controller;

import lombok.RequiredArgsConstructor;
import org.example.edusoft.learning.Result;
import org.example.edusoft.learning.entity.SelfAnswer;
import org.example.edusoft.learning.entity.SelfSubmission;
import org.example.edusoft.learning.mapper.SelfAnswerMapper;
import org.example.edusoft.learning.mapper.SelfSubmissionMapper;
import org.example.edusoft.learning.service.SelfPracticeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/learning/self-practice")
@RequiredArgsConstructor
public class SelfPracticeController {

    private static final Logger logger = LoggerFactory.getLogger(SelfPracticeController.class);

    // private final AiAssistantService aiAssistantService; // AI Service is external
    private final SelfPracticeService selfPracticeService;
    private final SelfSubmissionMapper submissionMapper;
    private final SelfAnswerMapper answerMapper;

    @PostMapping("/save-progress")
    public Result<Boolean> saveProgress(@RequestBody Map<String, Object> req, @RequestHeader("X-User-Id") Long studentId) {
        logger.info("Received request to save practice progress for student {}: {}", studentId, req);
        // In a real scenario, this would save the progress to a database.
        // For now, we just acknowledge the request.
        return Result.success(true, "Progress saved successfully.");
    }

    @PostMapping("/submit")
    public Result<Map<String, Object>> submitPractice(@RequestBody Map<String, Object> req, @RequestHeader("X-User-Id") Long studentId) {
        logger.info("Received practice submission from student {}: {}", studentId, req);

        Long practiceId = ((Number) req.getOrDefault("practiceId", 0)).longValue();
        if (!selfPracticeService.checkPracticeExists(practiceId)) {
            return Result.error("Submission failed: Practice ID does not exist.");
        }

        List<Map<String, Object>> answers = (List<Map<String, Object>>) req.get("answers");
        if (answers == null || answers.isEmpty()) {
            return Result.error("Answer list cannot be empty.");
        }

        SelfSubmission submission = new SelfSubmission();
        submission.setPracticeId(practiceId);
        submission.setStudentId(studentId);
        submissionMapper.insertSubmission(submission);
        Long submissionId = submission.getId();

        for (Map<String, Object> ans : answers) {
            SelfAnswer selfAnswer = new SelfAnswer();
            selfAnswer.setSubmissionId(submissionId);
            selfAnswer.setQuestionId(((Number) ans.get("questionId")).longValue());
            selfAnswer.setAnswer((String) ans.get("answer"));
            answerMapper.insertAnswer(selfAnswer);
        }

        // Auto-grading logic would go here.
        // For now, just returning a success message.
        return Result.success(null, "Practice submitted successfully. Awaiting grading.");
    }

    @PostMapping("/generate")
    public Result<Map<String, Object>> generatePractice(@RequestBody Map<String, Object> req, @RequestHeader("X-User-Id") Long studentId) {
        logger.info("Received request to generate practice for student {}: {}", studentId, req);
        // This would call the AI service to generate questions.
        // Map<String, Object> aiResult = aiAssistantService.generateQuestions(req);
        // Long practiceId = selfPracticeService.saveGeneratedPractice(studentId, aiResult);
        // aiResult.put("practiceId", practiceId);
        // return Result.success(aiResult, "练习生成成功");
        return Result.error("AI service integration is not yet implemented.");
    }

    @GetMapping("/history")
    public Result<List<Map<String, Object>>> getHistory(@RequestHeader("X-User-Id") Long studentId) {
        List<Map<String, Object>> history = selfPracticeService.getHistory(studentId);
        return Result.success(history, "Successfully retrieved practice history.");
    }

    @GetMapping("/detail/{practiceId}")
    public Result<List<Map<String, Object>>> getDetail(@PathVariable Long practiceId, @RequestHeader("X-User-Id") Long studentId) {
        List<Map<String, Object>> detail = selfPracticeService.getDetail(studentId, practiceId);
        return Result.success(detail, "Successfully retrieved practice details.");
    }
}
