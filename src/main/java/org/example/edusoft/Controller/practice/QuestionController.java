package org.example.edusoft.controller.practice;

import org.example.edusoft.entity.practice.Question;
import org.example.edusoft.entity.practice.QuestionListDTO;
import org.example.edusoft.service.practice.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/practice/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createQuestion(@RequestBody Question question) {
        Question createdQuestion = questionService.createQuestion(question);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "题目创建成功");
        response.put("data", Map.of("questionId", createdQuestion.getId()));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateQuestion(@PathVariable Long id, @RequestBody Question question) {
        question.setId(id);
        Question updatedQuestion = questionService.updateQuestion(question);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "题目更新成功");
        response.put("data", updatedQuestion);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getQuestionList(@RequestParam(required = false) Long courseId) {
        List<QuestionListDTO> questions;
        if (courseId != null) {
            questions = questionService.getQuestionListByCourse(courseId);
        } else {
            questions = questionService.getAllQuestions();
        }
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取题目列表成功");
        response.put("data", Map.of("questions", questions));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getQuestionDetail(@PathVariable Long id) {
        Question question = questionService.getQuestionDetail(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取题目详情成功");
        response.put("data", question);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "题目删除成功");
        response.put("data", Map.of("questionId", id));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importQuestionsToPractice(@RequestBody Map<String, Object> request) {
        Long practiceId = Long.valueOf(request.get("practiceId").toString());
        @SuppressWarnings("unchecked")
        List<Integer> rawQuestionIds = (List<Integer>) request.get("questionIds");
        List<Long> questionIds = rawQuestionIds.stream()
                .map(Integer::longValue)
                .collect(Collectors.toList());
        @SuppressWarnings("unchecked")
        List<Integer> scores = (List<Integer>) request.get("scores");
        questionService.importQuestionsToPractice(practiceId, questionIds, scores);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "题目导入成功");
        response.put("data", Map.of(
            "practiceId", practiceId,
            "questionCount", questionIds.size()
        ));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/section")
    public ResponseEntity<Map<String, Object>> getQuestionsBySection(
            @RequestParam Long courseId,
            @RequestParam Long sectionId) {
        List<Question> questions = questionService.getQuestionsBySection(courseId, sectionId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取章节题目列表成功");
        response.put("data", questions);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchCreateQuestions(@RequestBody List<Question> questions) {
        List<Question> createdQuestions = questionService.batchCreateQuestions(questions);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "批量创建题目成功");
        response.put("data", createdQuestions);
        return ResponseEntity.ok(response);
    }
} 