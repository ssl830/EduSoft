package org.example.edusoft.learning.controller;

import cn.dev33.satoken.stp.StpUtil;
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
@RequestMapping("/api/practice")
@RequiredArgsConstructor
public class PracticeController {

    private final PracticeService practiceService;

    @PostMapping("/create")
    public Result<Map<String, Object>> createPractice(@RequestBody Practice practice) {
        // 暂时允许游客访问进行测试
        try {
            if (!StpUtil.isLogin()) {
                System.out.println("Warning: 未认证的练习创建访问，使用默认用户ID: 1");
                practice.setCreatedBy(1L); // 使用默认用户ID进行测试
            } else {
                Long userId = StpUtil.getLoginIdAsLong();
                practice.setCreatedBy(userId);
            }
            
            System.out.println("创建练习，参数: " + practice);
            Practice createdPractice = practiceService.createPractice(practice);
            System.out.println("练习创建成功，ID: " + createdPractice.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("practiceId", createdPractice.getId());
            return Result.success(response, "练习创建成功");
        } catch (Exception e) {
            System.out.println("创建练习失败: " + e.getMessage());
            e.printStackTrace();
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

    @GetMapping("/list")
    public Result<List<Practice>> getPracticeList(@RequestParam Long classId) {
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
    public Result<Void> addQuestionToPractice(@PathVariable Long practiceId, @PathVariable Long questionId, @RequestParam Integer score) {
        try {
            practiceService.addQuestionToPractice(practiceId, questionId, score);
            return Result.success(null, "添加题目成功");
        } catch (Exception e) {
            return Result.error("添加题目失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{practiceId}/questions/{questionId}")
    public Result<Void> removeQuestionFromPractice(@PathVariable Long practiceId, @PathVariable Long questionId) {
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
    public Result<Void> favoriteQuestion(@PathVariable Long questionId) {
        if (!StpUtil.isLogin()) {
            return Result.error("请先登录");
        }
        Long studentId = StpUtil.getLoginIdAsLong();
        try {
            practiceService.favoriteQuestion(studentId, questionId);
            return Result.success(null, "收藏成功");
        } catch (Exception e) {
            return Result.error("收藏失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/questions/{questionId}/favorite")
    public Result<Void> unfavoriteQuestion(@PathVariable Long questionId) {
        if (!StpUtil.isLogin()) {
            return Result.error("请先登录");
        }
        Long studentId = StpUtil.getLoginIdAsLong();
        try {
            practiceService.unfavoriteQuestion(studentId, questionId);
            return Result.success(null, "取消收藏成功");
        } catch (Exception e) {
            return Result.error("取消收藏失败：" + e.getMessage());
        }
    }

    @GetMapping("/questions/favorites")
    public Result<List<Map<String, Object>>> getFavoriteQuestions() {
        if (!StpUtil.isLogin()) {
            return Result.error("请先登录");
        }
        Long studentId = StpUtil.getLoginIdAsLong();
        List<Map<String, Object>> questions = practiceService.getFavoriteQuestions(studentId);
        return Result.success(questions);
    }

    // 添加错题
    @PostMapping("/questions/{questionId}/wrong")
    public Result<Boolean> addWrongQuestion(@PathVariable Long questionId, @RequestBody Map<String, String> data) {
        if (!StpUtil.isLogin()) {
            return Result.error("请先登录");
        }
        Long studentId = StpUtil.getLoginIdAsLong();
        String wrongAnswer = data.get("wrongAnswer");
        practiceService.addWrongQuestion(studentId, questionId, wrongAnswer);
        return Result.success(true);
    }

    // 获取错题列表
    @GetMapping("/questions/wrong")
    public Result<List<Map<String, Object>>> getWrongQuestions(@RequestHeader("X-User-Id") Long studentId) {
        List<Map<String, Object>> questions = practiceService.getWrongQuestions(studentId);
        return Result.success(questions);
    }

    // 获取某个课程的错题列表
    @GetMapping("/questions/wrong/course/{courseId}")
    public Result<List<Map<String, Object>>> getWrongQuestionsByCourse(
            @RequestHeader("X-User-Id") Long studentId,
            @PathVariable Long courseId) {
        List<Map<String, Object>> questions = practiceService.getWrongQuestionsByCourse(studentId, courseId);
        return Result.success(questions);
    }

    // 删除错题
    @DeleteMapping("/questions/{questionId}/wrong")
    public Result<Boolean> removeWrongQuestion(
            @RequestHeader("X-User-Id") Long studentId,
            @PathVariable Long questionId) {
        practiceService.removeWrongQuestion(studentId, questionId);
        return Result.success(true);
    }

    // 获取课程的所有练习
    @GetMapping("/course/{courseId}")
    public Result<List<Map<String, Object>>> getCoursePractices(@PathVariable Long courseId) {
        try {
            // 如果用户已登录，使用登录用户ID；否则使用默认值0（游客模式）
            Long studentId = 0L;
            try {
                if (StpUtil.isLogin()) {
                    studentId = StpUtil.getLoginIdAsLong();
                }
            } catch (Exception ignored) {
                // 忽略认证异常，使用游客模式
            }
            List<Map<String, Object>> practices = practiceService.getCoursePractices(studentId, courseId);
            return Result.success(practices);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "获取课程练习失败：" + e.getMessage());
        }
    }

    @GetMapping("/student/list")
    public Result<List<PracticeDTO>> getStudentPracticeList(
            @RequestParam Long studentId,
            @RequestParam Long classId) {
        try {
            List<PracticeDTO> practiceList = practiceService.getStudentPracticeList(studentId, classId);
            return Result.success(practiceList);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "获取练习列表失败：" + e.getMessage());
        }
    }

//
//    /**
//     * 老师端：获取班级所有练习列表
//     */
    @GetMapping("/list/teacher")
    public Result<List<Practice>> getPracticeListForTeacher(@RequestParam Long classId) {
        try {
            // 教师端接口，但允许游客访问进行测试
            try {
                if (!StpUtil.isLogin()) {
                    // 暂时允许未认证的访问用于测试
                    System.out.println("Warning: 未认证的教师端访问");
                }
            } catch (Exception e) {
                System.out.println("Sa-Token 认证异常: " + e.getMessage());
            }
            List<Practice> practices = practiceService.getPracticeList(classId);
            return Result.success(practices, "获取练习列表成功");
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "获取练习列表失败：" + e.getMessage());
        }
    }

    @PutMapping("/{practiceId}/questions/{questionId}")
    public Result<Void> updateQuestionScore(
            @PathVariable Long practiceId,
            @PathVariable Long questionId,
            @RequestParam Integer score) {
        System.out.println("=== updateQuestionScore 开始 ===");
        System.out.println("practiceId: " + practiceId);
        System.out.println("questionId: " + questionId);
        System.out.println("score: " + score);
        
        try {
            // 教师端接口，但暂时允许游客访问进行测试
            try {
                if (!StpUtil.isLogin()) {
                    System.out.println("Warning: 未认证的题目分值更新访问");
                }
            } catch (Exception e) {
                System.out.println("Sa-Token 认证异常: " + e.getMessage());
            }
            
            // 验证参数
            if (score == null) {
                System.out.println("分值为null");
                return Result.error(400, "分值不能为空");
            }
            if (score <= 0) {
                System.out.println("分值小于等于0: " + score);
                return Result.error(400, "分值必须大于0");
            }
            
            System.out.println("调用 practiceService.addQuestionToPractice");
            practiceService.addQuestionToPractice(practiceId, questionId, score);
            System.out.println("题目分值更新成功");
            return Result.success(null, "题目分值更新成功");
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException: " + e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            return Result.error(500, "题目分值更新失败：" + e.getMessage());
        }
    }
//
//    /**
//     * 获取教师相关的所有练习信息
//     */
//    @GetMapping("/teacher/practices")
//    public Result<List<Map<String, Object>>> getTeacherPractices() {
//        if (!StpUtil.isLogin()) {
//            return Result.error("请先登录");
//        }
//        Long teacherId = StpUtil.getLoginIdAsLong();
//        List<Map<String, Object>> practices = practiceService.getTeacherPractices(teacherId);
//        return Result.success(practices, "获取教师练习信息成功");
//    }
//
//    @GetMapping("/stats/{practiceId}")
//    public Result<Map<String, Object>> getPracticeStats(@PathVariable Long practiceId) {
//        Map<String, Object> stats = practiceService.getSubmissionStats(practiceId);
//        return Result.success(stats);
//    }
//
//    /**
//     * 手动触发：统计并写入练习每题得分率
//     */
//    @PostMapping("/update-score-rate/{practiceId}")
//    public Result<String> updateScoreRate(@PathVariable Long practiceId) {
//        try {
//            practiceService.updateScoreRateAfterDeadline(practiceId);
//            return Result.success("OK", "得分率统计并写入成功");
//        } catch (Exception e) {
//            return Result.error(500, "得分率统计失败：" + e.getMessage());
//        }
//    }
//
//    /**
//     * 自动定时任务：每天凌晨1点检查所有已截止练习，自动统计得分率
//     * 需在主类加@EnableScheduling
//     */
//    @Scheduled(cron = "0 0 1 * * ?")
//    public void autoUpdateScoreRateForAllPractices() {
//        // 伪代码：实际应查找所有已截止且未统计的练习ID
//        List<Long> practiceIds = practiceService.getAllEndedPracticeIds();
//        for (Long pid : practiceIds) {
//            practiceService.updateScoreRateAfterDeadline(pid);
//        }
//    }
}
