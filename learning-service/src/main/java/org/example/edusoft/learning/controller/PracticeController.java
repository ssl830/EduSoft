package org.example.edusoft.learning.controller;

import lombok.RequiredArgsConstructor;
import org.example.edusoft.learning.Result;
import org.example.edusoft.learning.dto.PracticeDTO;
import org.example.edusoft.learning.entity.Practice;
import org.example.edusoft.learning.entity.Question;
import org.example.edusoft.learning.service.PracticeService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/learning/practice")
@RequiredArgsConstructor
public class PracticeController {

    private final PracticeService practiceService;

    @PostMapping
    public Result<Map<String, Object>> createPractice(@RequestBody Practice practice, @RequestHeader("X-User-Id") Long userId) {
        try {
            practice.setCreatedBy(userId);
            Practice createdPractice = practiceService.createPractice(practice);
            Map<String, Object> response = new HashMap<>();
            response.put("practiceId", createdPractice.getId());
            return Result.success(response, "练习创建成功");
        } catch (Exception e) {
            return Result.error("创建练习失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Practice> updatePractice(@PathVariable Long id, @RequestBody Practice practice) {
        try {
            practice.setId(id);
            Practice updatedPractice = practiceService.updatePractice(practice);
            return Result.success(updatedPractice, "练习更新成功");
        } catch (Exception e) {
            return Result.error("更新练习失败：" + e.getMessage());
        }
    }

    @GetMapping("/class/{classId}")
    public Result<List<Practice>> getPracticeList(@PathVariable Long classId) {
        try {
            List<Practice> practices = practiceService.getPracticeList(classId);
            return Result.success(practices, "获取练习列表成功");
        } catch (Exception e) {
            return Result.error("获取练习列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Practice> getPracticeDetail(@PathVariable Long id) {
        try {
            Practice practice = practiceService.getPracticeDetail(id);
            return Result.success(practice, "获取练习详情成功");
        } catch (Exception e) {
            return Result.error("获取练习详情失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deletePractice(@PathVariable Long id) {
        try {
            practiceService.deletePractice(id);
            return Result.success(null, "练习删除成功");
        } catch (Exception e) {
            return Result.error("删除练习失败：" + e.getMessage());
        }
    }

    @PostMapping("/{practiceId}/questions/{questionId}")
    public Result<Void> addQuestionToPractice(
            @PathVariable Long practiceId,
            @PathVariable Long questionId,
            @RequestParam Integer score) {
        try {
            practiceService.addQuestionToPractice(practiceId, questionId, score);
            return Result.success(null, "添加题目成功");
        } catch (Exception e) {
            return Result.error("添加题目失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{practiceId}/questions/{questionId}")
    public Result<Void> removeQuestionFromPractice(
            @PathVariable Long practiceId,
            @PathVariable Long questionId) {
        try {
            practiceService.removeQuestionFromPractice(practiceId, questionId);
            return Result.success(null, "移除题目成功");
        } catch (Exception e) {
            return Result.error("移除题目失败：" + e.getMessage());
        }
    }

    @GetMapping("/{practiceId}/questions")
    public Result<List<Question>> getPracticeQuestions(@PathVariable Long practiceId) {
        try {
            List<Question> questions = practiceService.getPracticeQuestions(practiceId);
            return Result.success(questions, "获取练习题目成功");
        } catch (Exception e) {
            return Result.error("获取练习题目失败：" + e.getMessage());
        }
    }

    @PostMapping("/questions/{questionId}/favorite")
    public Result<Void> favoriteQuestion(@PathVariable Long questionId, @RequestHeader("X-User-Id") Long studentId) {
        try {
            practiceService.favoriteQuestion(studentId, questionId);
            return Result.success(null, "收藏成功");
        } catch (Exception e) {
            return Result.error("收藏失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/questions/{questionId}/favorite")
    public Result<Void> unfavoriteQuestion(@PathVariable Long questionId, @RequestHeader("X-User-Id") Long studentId) {
        try {
            practiceService.unfavoriteQuestion(studentId, questionId);
            return Result.success(null, "取消收藏成功");
        } catch (Exception e) {
            return Result.error("取消收藏失败：" + e.getMessage());
        }
    }

    @GetMapping("/favorites")
    public Result<List<Map<String, Object>>> getFavoriteQuestions(@RequestHeader("X-User-Id") Long studentId) {
        try {
            List<Map<String, Object>> questions = practiceService.getFavoriteQuestions(studentId);
            return Result.success(questions, "获取收藏列表成功");
        } catch (Exception e) {
            return Result.error("获取收藏列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/wrong-questions")
    public Result<List<Map<String, Object>>> getWrongQuestions(@RequestHeader("X-User-Id") Long studentId) {
        try {
            List<Map<String, Object>> questions = practiceService.getWrongQuestions(studentId);
            return Result.success(questions, "获取错题列表成功");
        } catch (Exception e) {
            return Result.error("获取错题列表失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/student/list")
    public Result<List<PracticeDTO>> getStudentPracticeList(@RequestHeader("X-User-Id") Long studentId, @RequestParam Long classId) {
        try {
            List<PracticeDTO> practiceList = practiceService.getStudentPracticeList(studentId, classId);
            return Result.success(practiceList, "获取学生练习列表成功");
        } catch (Exception e) {
            return Result.error("获取学生练习列表失败：" + e.getMessage());
        }
    }
}
