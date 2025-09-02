package org.example.edusoft.learning.controller;

import lombok.RequiredArgsConstructor;
import org.example.edusoft.learning.Result;
import org.example.edusoft.learning.entity.SelfAnswer;
import org.example.edusoft.learning.entity.SelfSubmission;
import org.example.edusoft.learning.entity.Question;
import org.example.edusoft.learning.mapper.SelfAnswerMapper;
import org.example.edusoft.learning.mapper.SelfSubmissionMapper;
import org.example.edusoft.learning.service.SelfPracticeService;
import org.example.edusoft.learning.service.QuestionService;
import org.example.edusoft.learning.ai.AIServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/api/selfpractice")
@RequiredArgsConstructor
public class SelfPracticeController {

    private static final Logger logger = LoggerFactory.getLogger(SelfPracticeController.class);

    private final SelfPracticeService selfPracticeService;
    private final SelfSubmissionMapper submissionMapper;
    private final SelfAnswerMapper answerMapper;
    private final QuestionService questionService;
    private final AIServiceClient aiServiceClient;

    /**
     * 暂存练习进度，不进行评测
     */
    @PostMapping("/save-progress")
    public Result<Boolean> saveProgress(@RequestBody Map<String, Object> req, @RequestHeader("X-User-Id") Long studentId) {
        logger.info("收到学生练习暂存请求 - 学生ID: {}, 请求: {}", studentId, req);
        // TODO: 将进度保存至 SelfProgress 表（待持久化实现）
        return Result.success(true, "进度已暂存");
    }

    /**
     * 提交练习并自动评测
     */
    @PostMapping("/submit")
    public Result<Map<String, Object>> submitPractice(@RequestBody Map<String, Object> req, @RequestHeader("X-User-Id") Long studentId) {
        logger.info("收到学生练习提交请求 - 学生ID: {}, 请求: {}", studentId, req);

        Long practiceId = ((Number) req.getOrDefault("practiceId", 0)).longValue();
        logger.info("学生自测练习提交 - 学生ID: {}, 练习ID: {}", studentId, practiceId);
        
        // 检查 practiceId 是否存在于 SelfPractice 表
        try {
            boolean exists = selfPracticeService.checkPracticeExists(practiceId);
            if (!exists) {
                logger.error("学生自测练习提交失败 - 练习ID不存在: {}", practiceId);
                return Result.error("提交失败：练习ID不存在");
            }
            logger.info("学生自测练习提交 - 练习ID校验通过");
        } catch (Exception e) {
            logger.error("学生自测练习提交 - 检查练习ID是否存在时发生异常: {}", e.getMessage(), e);
            return Result.error("系统异常，请稍后重试");
        }
        
        List<Map<String, Object>> answers = (List<Map<String, Object>>) req.get("answers");
        if (answers == null || answers.isEmpty()) {
            return Result.error("答案列表不能为空");
        }

        double totalScore = 0;
        double totalPossible = 0;
        List<Map<String, Object>> detailList = new ArrayList<>();
        int order = 1;
        List<SelfAnswer> tempAnswers = new ArrayList<>();

        for (Map<String, Object> ans : answers) {
            Long qid = ((Number) ans.get("questionId")).longValue();
            String stuAns = (String) ans.getOrDefault("answer", "");
            Question q = null;
            try {
                if (qid != 0) {
                    q = questionService.getQuestionById(qid);
                }
            } catch (Exception ignored) {}

            int questionScore = ans.get("score") instanceof Number ? ((Number) ans.get("score")).intValue() : 10; // 默认分值
            boolean correct = false;
            String feedback = "";
            String correctAns = "";
            double obtained = 0;

            String qTypeStr = q != null ? q.getType().name() : (String) ans.getOrDefault("type", "singlechoice");
            
            // 根据题型进行评分
            if ("singlechoice".equals(qTypeStr) || "judge".equals(qTypeStr) || "fillblank".equals(qTypeStr)) {
                if (q != null) {
                    correctAns = q.getAnswer() == null ? "" : q.getAnswer().trim();
                } else {
                    correctAns = ans.getOrDefault("correctAnswer", "").toString().trim();
                }
                if (correctAns.equalsIgnoreCase(stuAns.trim())) {
                    correct = true;
                    obtained = questionScore;
                }
            } else {
                // 主观题使用AI评分
                String questionContent = q != null ? q.getContent() : ans.getOrDefault("question", "").toString();
                String referenceAnswer = q != null ? q.getAnswer() : ans.getOrDefault("correctAnswer", "").toString();
                
                if (!questionContent.isBlank()) {
                    try {
                        Map<String, Object> eval = aiServiceClient.evaluateSubjectiveAnswer(questionContent, stuAns, referenceAnswer);
                        if (eval != null && eval.get("score") != null) {
                            obtained = ((Number) eval.get("score")).doubleValue();
                            correct = obtained >= questionScore * 0.6; // 60% 以上算正确
                            feedback = eval.getOrDefault("feedback", "").toString();
                        }
                    } catch (Exception e) {
                        logger.warn("AI评分异常，使用默认评分 - 问题ID: {}, 错误: {}", qid, e.getMessage());
                        obtained = questionScore * 0.5; // 默认给一半分数
                        feedback = "系统评分异常，请联系老师人工评分";
                    }
                }
            }

            totalScore += obtained;
            totalPossible += questionScore;

            Map<String, Object> item = new HashMap<>();
            item.put("questionId", qid);
            item.put("correct", correct);
            item.put("score", obtained);
            item.put("feedback", feedback);
            item.put("maxScore", questionScore);
            item.put("correctAnswer", correctAns);
            detailList.add(item);

            // 保存学生答案到 SelfAnswer
            SelfAnswer selfAns = new SelfAnswer();
            selfAns.setQuestionId(qid);
            selfAns.setAnswerText(stuAns);
            selfAns.setCorrect(correct);
            selfAns.setScore((int) Math.round(obtained));
            selfAns.setIsJudged(true);
            selfAns.setSortOrder(order++);
            tempAnswers.add(selfAns);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalScore", totalScore);
        result.put("totalPossible", totalPossible);
        result.put("practiceId", practiceId);
        result.put("details", detailList);

        // 保存 SelfSubmission & SelfAnswer
        SelfSubmission submission = new SelfSubmission();
        submission.setSelfPracticeId(practiceId);
        submission.setStudentId(studentId);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setScore((int) Math.round(totalScore));
        submission.setIsJudged(true);
        
        try {
            logger.info("准备插入学生自测提交记录 - 学生ID: {}, 练习ID: {}, 得分: {}", 
                studentId, practiceId, (int) Math.round(totalScore));
            submissionMapper.insert(submission);
            logger.info("学生自测提交记录插入成功, 生成的提交ID: {}", submission.getId());
            
            Long submissionId = submission.getId();
            for (SelfAnswer a : tempAnswers) {
                a.setSubmissionId(submissionId);
                answerMapper.insert(a);
            }
            logger.info("成功插入 {} 条学生答案记录", tempAnswers.size());
        } catch (Exception e) {
            logger.error("插入学生自测提交记录失败: {}", e.getMessage(), e);
            return Result.error("提交失败：" + e.getMessage());
        }

        return Result.success(result, "评分完成");
    }

    /**
     * 生成AI练习
     */
    @PostMapping("/generate")
    public Result<Map<String, Object>> generatePractice(@RequestBody Map<String, Object> req, @RequestHeader("X-User-Id") Long studentId) {
        logger.info("收到生成练习请求 - 学生ID: {}, 请求: {}", studentId, req);
        
        try {
            String prompt = (String) req.get("prompt");
            if (prompt == null || prompt.trim().isEmpty()) {
                return Result.error("请求内容不能为空");
            }

            // 调用AI服务生成练习
            Map<String, Object> aiResult = aiServiceClient.generateExercise(prompt);
            if (aiResult == null || aiResult.isEmpty()) {
                return Result.error("AI服务生成失败，请稍后重试");
            }

            Long practiceId = null;
            // 保存生成的练习并获取practiceId
            practiceId = selfPracticeService.saveGeneratedPractice(studentId, aiResult);

            // 在返回结果中添加practiceId
            Map<String, Object> responseData = new HashMap<>(aiResult);
            if (practiceId != null) {
                responseData.put("practiceId", practiceId);
            }

            return Result.success(responseData, "练习生成成功");
        } catch (Exception e) {
            logger.error("生成练习失败 - 学生ID: {}, 错误: {}", studentId, e.getMessage(), e);
            return Result.error("生成练习失败：" + e.getMessage());
        }
    }

    @GetMapping("/history")
    public Result<List<Map<String, Object>>> getHistory(@RequestHeader("X-User-Id") Long studentId) {
        List<Map<String, Object>> history = selfPracticeService.getHistory(studentId);
        return Result.success(history, "获取练习历史成功");
    }

    @GetMapping("/detail/{practiceId}")
    public Result<List<Map<String, Object>>> getDetail(@PathVariable Long practiceId, @RequestHeader("X-User-Id") Long studentId) {
        List<Map<String, Object>> detail = selfPracticeService.getDetail(studentId, practiceId);
        return Result.success(detail, "获取练习详情成功");
    }
}
