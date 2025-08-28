package org.example.edusoft.controller.practice;

import org.example.edusoft.common.Result;
import org.example.edusoft.entity.practice.Practice;
import org.example.edusoft.entity.practice.Question;
import org.example.edusoft.entity.practice.PracticeListDTO;
import org.example.edusoft.service.practice.PracticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.dev33.satoken.stp.StpUtil;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.scheduling.annotation.Scheduled;

@RestController
@RequestMapping("/api/practice")
public class PracticeController {

    @Autowired
    private PracticeService practiceService;
 
    @PostMapping("/create")
    public Result<Map<String, Object>> createPractice(@RequestBody Practice practice) {
        try {
            Practice createdPractice = practiceService.createPractice(practice);
            Map<String, Object> response = new HashMap<>();
            response.put("practiceId", createdPractice.getId());
            return Result.success(response, "练习创建成功");
        } catch (Exception e) {
            return Result.error(500, "创建练习失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Practice> updatePractice(@PathVariable Long id, @RequestBody Practice practice) {
        try {
            practice.setId(id);
            Practice updatedPractice = practiceService.updatePractice(practice);
            return Result.success(updatedPractice, "练习更新成功");
        } catch (Exception e) {
            return Result.error(500, "更新练习失败：" + e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<List<Practice>> getPracticeList(@RequestParam Long classId) {
        try {
            List<Practice> practices = practiceService.getPracticeList(classId);
            return Result.success(practices, "获取练习列表成功");
        } catch (Exception e) {
            return Result.error(500, "获取练习列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Practice> getPracticeDetail(@PathVariable Long id) {
        try {
            Practice practice = practiceService.getPracticeDetail(id);
            return Result.success(practice, "获取练习详情成功");
        } catch (Exception e) {
            return Result.error(500, "获取练习详情失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deletePractice(@PathVariable Long id) {
        try {
            practiceService.deletePractice(id);
            return Result.success(null, "练习删除成功");
        } catch (Exception e) {
            return Result.error(500, "删除练习失败：" + e.getMessage());
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
            return Result.error(500, "添加题目失败：" + e.getMessage());
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
            return Result.error(500, "移除题目失败：" + e.getMessage());
        }
    }

    @GetMapping("/{practiceId}/questions")
    public Result<List<Question>> getPracticeQuestions(@PathVariable Long practiceId) {
        try {
            List<Question> questions = practiceService.getPracticeQuestions(practiceId);
            return Result.success(questions, "获取练习题目列表成功");
        } catch (Exception e) {
            return Result.error(500, "获取练习题目列表失败：" + e.getMessage());
        }
    }

    // 收藏题目
    @PostMapping("/questions/{questionId}/favorite")
    public Result<Boolean> favoriteQuestion(@PathVariable Long questionId) {
        if (!StpUtil.isLogin()) {
            return Result.error("请先登录");
        }
        Long studentId = StpUtil.getLoginIdAsLong();
        practiceService.favoriteQuestion(studentId, questionId);
        return Result.success(true);
    }

    // 取消收藏题目
    @DeleteMapping("/questions/{questionId}/favorite")
    public Result<Boolean> unfavoriteQuestion(@PathVariable Long questionId) {
        if (!StpUtil.isLogin()) {
            return Result.error("请先登录");
        }
        Long studentId = StpUtil.getLoginIdAsLong();
        practiceService.unfavoriteQuestion(studentId, questionId);
        return Result.success(true);
    }

    // 获取收藏的题目列表
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
    public Result<Boolean> addWrongQuestion(
            @PathVariable Long questionId,
            @RequestBody Map<String, String> data) {
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
    public Result<List<Map<String, Object>>> getWrongQuestions() {
        if (!StpUtil.isLogin()) {
            return Result.error("请先登录");
        }
        Long studentId = StpUtil.getLoginIdAsLong();
        List<Map<String, Object>> questions = practiceService.getWrongQuestions(studentId);
        return Result.success(questions);
    }

    // 获取某个课程的错题列表
    @GetMapping("/questions/wrong/course/{courseId}")
    public Result<List<Map<String, Object>>> getWrongQuestionsByCourse(@PathVariable Long courseId) {
        if (!StpUtil.isLogin()) {
            return Result.error("请先登录");
        }
        Long studentId = StpUtil.getLoginIdAsLong();
        List<Map<String, Object>> questions = practiceService.getWrongQuestionsByCourse(studentId, courseId);
        return Result.success(questions);
    }

    // 删除错题
    @DeleteMapping("/questions/{questionId}/wrong")
    public Result<Boolean> removeWrongQuestion(@PathVariable Long questionId) {
        if (!StpUtil.isLogin()) {
            return Result.error("请先登录");
        }
        Long studentId = StpUtil.getLoginIdAsLong();
        practiceService.removeWrongQuestion(studentId, questionId);
        return Result.success(true);
    }

    // 获取课程的所有练习
    @GetMapping("/course/{courseId}")
    public Result<List<Map<String, Object>>> getCoursePractices(@PathVariable Long courseId) {
        if (!StpUtil.isLogin()) {
            return Result.error("请先登录");
        }
        Long studentId = StpUtil.getLoginIdAsLong();
        List<Map<String, Object>> practices = practiceService.getCoursePractices(studentId, courseId);
        return Result.success(practices);
    }

    /**
     * 获取学生的练习列表
     * @param studentId 学生ID
     * @param classId 班级ID
     * @return 练习列表，包含完成状态
     */
    @GetMapping("/student/list")
    public Result<List<PracticeListDTO>> getStudentPracticeList(
            @RequestParam Long studentId,
            @RequestParam Long classId) {
        try {
            List<PracticeListDTO> practiceList = practiceService.getStudentPracticeList(studentId, classId);
            return Result.success(practiceList);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "获取练习列表失败：" + e.getMessage());
        }
    }

    /**
     * 老师端：获取班级所有练习列表
     */
    @GetMapping("/list/teacher")
    public Result<List<Practice>> getPracticeListForTeacher(@RequestParam Long classId) {
        try {
            List<Practice> practices = practiceService.getPracticeList(classId);
            return Result.success(practices, "获取练习列表成功");
        } catch (Exception e) {
            return Result.error(500, "获取练习列表失败：" + e.getMessage());
        }
    }

    @PutMapping("/{practiceId}/questions/{questionId}")
    public Result<Void> updateQuestionScore(
            @PathVariable Long practiceId,
            @PathVariable Long questionId,
            @RequestParam Integer score) {
        try {
            practiceService.addQuestionToPractice(practiceId, questionId, score);
            return Result.success(null, "题目分值更新成功");
        } catch (Exception e) {
            return Result.error(500, "题目分值更新失败：" + e.getMessage());
        }
    }

    /**
     * 获取教师相关的所有练习信息
     */
    @GetMapping("/teacher/practices")
    public Result<List<Map<String, Object>>> getTeacherPractices() {
        if (!StpUtil.isLogin()) {
            return Result.error("请先登录");
        }
        Long teacherId = StpUtil.getLoginIdAsLong();
        List<Map<String, Object>> practices = practiceService.getTeacherPractices(teacherId);
        return Result.success(practices, "获取教师练习信息成功");
    }

    @GetMapping("/stats/{practiceId}")
    public Result<Map<String, Object>> getPracticeStats(@PathVariable Long practiceId) {
        Map<String, Object> stats = practiceService.getSubmissionStats(practiceId);
        return Result.success(stats);
    }

    /**
     * 手动触发：统计并写入练习每题得分率
     */
    @PostMapping("/update-score-rate/{practiceId}")
    public Result<String> updateScoreRate(@PathVariable Long practiceId) {
        try {
            practiceService.updateScoreRateAfterDeadline(practiceId);
            return Result.success("OK", "得分率统计并写入成功");
        } catch (Exception e) {
            return Result.error(500, "得分率统计失败：" + e.getMessage());
        }
    }

    /**
     * 自动定时任务：每天凌晨1点检查所有已截止练习，自动统计得分率
     * 需在主类加@EnableScheduling
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void autoUpdateScoreRateForAllPractices() {
        // 伪代码：实际应查找所有已截止且未统计的练习ID
        List<Long> practiceIds = practiceService.getAllEndedPracticeIds();
        for (Long pid : practiceIds) {
            practiceService.updateScoreRateAfterDeadline(pid);
        }
    }
}