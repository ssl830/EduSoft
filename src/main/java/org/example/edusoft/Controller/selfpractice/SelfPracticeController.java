package org.example.edusoft.controller.selfpractice;

import org.example.edusoft.common.Result;
import org.example.edusoft.service.ai.AiAssistantService;
import org.example.edusoft.service.practice.QuestionService;
import org.example.edusoft.service.practice.PracticeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.dev33.satoken.stp.StpUtil;
import org.example.edusoft.mapper.selfpractice.SelfSubmissionMapper;
import org.example.edusoft.mapper.selfpractice.SelfAnswerMapper;
import org.example.edusoft.entity.selfpractice.SelfSubmission;
import org.example.edusoft.entity.selfpractice.SelfAnswer;
import org.example.edusoft.service.selfpractice.SelfPracticeService;

import java.util.Map;
import java.util.List;

/**
 * 学生自测练习相关接口
 * 注意：目前仅提供最小化实现，后续可完善持久化与评分逻辑
 */
@RestController
@RequestMapping("/api/selfpractice")
public class SelfPracticeController {

    private static final Logger logger = LoggerFactory.getLogger(SelfPracticeController.class);

    @Autowired
    private AiAssistantService aiAssistantService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private PracticeService practiceService;

    @Autowired
    private SelfSubmissionMapper submissionMapper;

    @Autowired
    private SelfAnswerMapper answerMapper;

    @Autowired
    private SelfPracticeService selfPracticeService;

    /**
     * 暂存练习进度，不进行评测
     * 前端提交：{
     *   "practiceId": 123,
     *   "answers": [{"questionId":1, "answer":"A"}]
     * }
     */
    @PostMapping("/save-progress")
    public Result<Boolean> saveProgress(@RequestBody Map<String, Object> req) {
        logger.info("收到学生练习暂存请求: {}", req);
        // TODO: 将进度保存至 SelfProgress 表（待持久化实现）
        return Result.success(true, "进度已暂存");
    }

    /**
     * 提交练习并自动评测
     * 前端提交：{
     *   "practiceId": 123,
     *   "answers": [{"questionId":1, "answer":"A", "type":"singlechoice"}]
     * }
     */
    @PostMapping("/submit")
    public Result<Map<String, Object>> submitPractice(@RequestBody Map<String, Object> req) {
        logger.info("收到学生练习提交请求: {}", req);

        // 用户登录校验（需要 Sa-Token）
        if (!StpUtil.isLogin()) {
            return Result.error("请先登录");
        }
        Long studentId = StpUtil.getLoginIdAsLong();

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
        }
        
        java.util.List<Map<String, Object>> answers = (java.util.List<Map<String, Object>>) req.get("answers");
        if (answers == null || answers.isEmpty()) {
            return Result.error("答案列表不能为空");
        }

        double totalScore = 0;
        double totalPossible = 0;
        java.util.List<Map<String, Object>> detailList = new java.util.ArrayList<>();
        int order = 1;
        java.util.List<SelfAnswer> tempAnswers = new java.util.ArrayList<>();

        for (Map<String, Object> ans : answers) {
            Long qid = ((Number) ans.get("questionId")).longValue();
            String stuAns = (String) ans.getOrDefault("answer", "");
            org.example.edusoft.entity.practice.Question q = null;
            try {
                if (qid != 0) {
                    q = questionService.getQuestionDetail(qid);
                }
            } catch (Exception ignored) {}

            int questionScore = ans.get("score") instanceof Number ? ((Number) ans.get("score")).intValue() : 10; // 默认分值
            boolean correct = false;
            String feedback = "";
            String correctAns = "";
            double obtained = 0;

            String qTypeStr = q != null ? q.getType().name() : (String) ans.getOrDefault("type", "singlechoice");
            org.example.edusoft.entity.practice.Question.QuestionType qType;
            try {
                qType = org.example.edusoft.entity.practice.Question.QuestionType.valueOf(qTypeStr);
            } catch (IllegalArgumentException e) {
                qType = org.example.edusoft.entity.practice.Question.QuestionType.singlechoice;
            }

            switch (qType) {
                case singlechoice:
                case judge:
                case fillblank:
                    
                    if (q != null) {
                        correctAns = q.getAnswer() == null ? "" : q.getAnswer().trim();
                    } else {
                        correctAns = ans.getOrDefault("correctAnswer", "").toString().trim();
                    }
                    if (correctAns.equalsIgnoreCase(stuAns.trim())) {
                        correct = true;
                        obtained = questionScore;
                    }
                    break;
                default:
                    String questionContent = q != null ? q.getContent() : ans.getOrDefault("question", "").toString();
                    String referenceAnswer = q != null ? q.getAnswer() : ans.getOrDefault("correctAnswer", "").toString();
                    Map<String, Object> eval = null;
                    if(!questionContent.isBlank()){
                        eval = aiAssistantService.evaluateSubjectiveAnswer(questionContent, stuAns, referenceAnswer, (double) questionScore);
                    }
                    if (eval != null && eval.get("score") != null) {
                        obtained = ((Number) eval.get("score")).doubleValue();
                        correct = obtained >= questionScore * 0.6; // 60% 以上算正确
                        feedback = eval.getOrDefault("feedback", "").toString();
                    }
                    break;
            }

            totalScore += obtained;
            totalPossible += questionScore;

            // 如果错误，写入 WrongQuestion
            if (!correct && qid != 0) {
                try {
                    practiceService.addWrongQuestion(studentId, qid, stuAns);
                } catch (Exception ignored) {}
            }

            java.util.Map<String, Object> item = new java.util.HashMap<>();
            item.put("questionId", qid);
            item.put("correct", correct);
            item.put("score", obtained);
            item.put("feedback", feedback);
            item.put("maxScore", questionScore);
            item.put("correctAnswer", correctAns);
            detailList.add(item);

            // 保存学生答案到 SelfAnswer
            SelfAnswer selfAns = new SelfAnswer();
            // submissionId 先占位, 稍后更新
            selfAns.setQuestionId(qid);
            selfAns.setAnswerText(stuAns);
            selfAns.setCorrect(correct);
            selfAns.setScore((int) Math.round(obtained));
            selfAns.setIsJudged(true);
            selfAns.setSortOrder(order++);
            // 临时放到列表, 提交后统一插入
            tempAnswers.add(selfAns);
        }

        Map<String, Object> result = new java.util.HashMap<>();
        result.put("totalScore", totalScore);
        result.put("totalPossible", totalPossible);
        result.put("practiceId", practiceId);
        result.put("details", detailList);

        // 保存 SelfSubmission & SelfAnswer
        SelfSubmission submission = new SelfSubmission();
        submission.setSelfPracticeId(practiceId);
        submission.setStudentId(studentId);
        submission.setSubmittedAt(java.time.LocalDateTime.now());
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

    @GetMapping("/history")
    public Result<?> listHistory(){
        Long id=StpUtil.getLoginIdAsLong();
        return Result.success(selfPracticeService.getHistory(id));
    }

    @GetMapping("/history/{pid}")
    public Result<?> detail(@PathVariable Long pid){
        Long id=StpUtil.getLoginIdAsLong();
        return Result.success(selfPracticeService.getDetail(id,pid));
    }
} 