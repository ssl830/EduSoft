package org.example.edusoft.learning.controller;

import lombok.RequiredArgsConstructor;
import org.example.edusoft.learning.Result;
import org.example.edusoft.learning.dto.QuestionDTO;
import org.example.edusoft.learning.entity.Question;
import org.example.edusoft.learning.service.QuestionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/learning/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public Result<Long> createQuestion(@RequestBody Question question, @RequestHeader("X-User-Id") Long userId) {
        question.setCreatorId(userId);
        Question createdQuestion = questionService.createQuestion(question);
        return Result.success(createdQuestion.getId(), "题目创建成功");
    }

    @PutMapping("/{id}")
    public Result<Question> updateQuestion(@PathVariable Long id, @RequestBody Question question) {
        question.setId(id);
        Question updatedQuestion = questionService.updateQuestion(question);
        return Result.success(updatedQuestion, "题目更新成功");
    }

    @GetMapping("/list")
    public Result<List<QuestionDTO>> getQuestionList(@RequestParam(required = false) Long courseId) {
        List<QuestionDTO> questions;
        if (courseId != null) {
            questions = questionService.getQuestionListByCourse(courseId);
        } else {
            questions = questionService.getAllQuestions();
        }
        return Result.success(questions, "获取题目列表成功");
    }

    @GetMapping("/{id}")
    public Result<Question> getQuestionDetail(@PathVariable Long id) {
        Question question = questionService.getQuestionDetail(id);
        return Result.success(question, "获取题目详情成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return Result.success(null, "题目删除成功");
    }

    @PostMapping("/import-to-practice")
    public Result<Void> importQuestionsToPractice(@RequestBody Map<String, Object> request) {
        Long practiceId = Long.valueOf(request.get("practiceId").toString());
        List<Long> questionIds = ((List<Integer>) request.get("questionIds")).stream().map(Integer::longValue).collect(Collectors.toList());
        List<Integer> scores = (List<Integer>) request.get("scores");
        questionService.importQuestionsToPractice(practiceId, questionIds, scores);
        return Result.success(null, "题目导入成功");
    }
}
