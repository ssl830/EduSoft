package org.example.edusoft.learning.controller;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.example.edusoft.learning.Result;
import org.example.edusoft.learning.dto.QuestionDTO;
import org.example.edusoft.learning.dto.QuestionListDTO;
import org.example.edusoft.learning.entity.Question;
import org.example.edusoft.learning.service.QuestionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/practice/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/create")
        public Result<Map<String, Object>> createQuestion(@RequestBody Question question) {
            Question createdQuestion = questionService.createQuestion(question);
            Map<String, Object> data = new HashMap<>();
            data.put("questionId", createdQuestion.getId());
            return Result.success(data, "题目创建成功");
    }

    @PutMapping("/{id}")
        public Result<Map<String, Object>> updateQuestion(@PathVariable Long id, @RequestBody Question question) {
            question.setId(id);
            Question updatedQuestion = questionService.updateQuestion(question);
            Map<String, Object> data = new HashMap<>();
            data.put("question", updatedQuestion);
            return Result.success(data, "题目更新成功");
    }

    @GetMapping("/list")
        public Result<Map<String, Object>> getQuestionList(@RequestParam(required = false) Long courseId) {
            List<QuestionListDTO> questions;
            if (courseId != null) {
                questions = questionService.getQuestionListByCourse(courseId);
            } else {
                questions = questionService.getAllQuestions();
            }
            Map<String, Object> data = new HashMap<>();
            data.put("questions", questions);
            return Result.success(data, "获取题目列表成功");
    }

    @GetMapping("/{id}")
        public Result<Map<String, Object>> getQuestionDetail(@PathVariable Long id) {
            Question question = questionService.getQuestionDetail(id);
            Map<String, Object> data = new HashMap<>();
            data.put("question", question);
            return Result.success(data, "获取题目详情成功");
    }

    @DeleteMapping("/{id}")
        public Result<Map<String, Object>> deleteQuestion(@PathVariable Long id) {
            questionService.deleteQuestion(id);
            Map<String, Object> data = new HashMap<>();
            data.put("questionId", id);
            return Result.success(data, "题目删除成功");
    }

    @PostMapping("/import")
    public Result<Map<String, Object>> importQuestionsToPractice(@RequestBody Map<String, Object> request) {
        Long practiceId = Long.valueOf(request.get("practiceId").toString());
        List<Long> questionIds = ((List<Integer>) request.get("questionIds")).stream().map(Integer::longValue).collect(Collectors.toList());
        List<Integer> scores = (List<Integer>) request.get("scores");
        questionService.importQuestionsToPractice(practiceId, questionIds, scores);
        Map<String, Object> data = new HashMap<>();
        data.put("practiceId", practiceId);
        data.put("questionCount", questionIds.size());
        return Result.success(data, "题目导入成功");
    }

    /**
     * 按章节获取题目列表
     */
    @GetMapping("/section")
        public Result<Map<String, Object>> getQuestionsBySection(@RequestParam Long courseId, @RequestParam Long sectionId) {
            List<Question> questions = questionService.getQuestionsBySection(courseId, sectionId);
            Map<String, Object> data = new HashMap<>();
            data.put("questions", questions);
            return Result.success(data, "获取章节题目列表成功");
    }

    /**
     * 批量创建题目
     */
    @PostMapping("/batch")
        public Result<Map<String, Object>> batchCreateQuestions(@RequestBody List<Question> questions) {
            List<Question> createdQuestions = questionService.batchCreateQuestions(questions);
            Map<String, Object> data = new HashMap<>();
            data.put("questions", createdQuestions);
            return Result.success(data, "批量创建题目成功");
    }
}
