package org.example.edusoft.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.edusoft.learning.entity.SelfPractice;
import org.example.edusoft.learning.entity.SelfAnswer;
import org.example.edusoft.learning.entity.SelfSubmission;
import org.example.edusoft.learning.entity.SelfPracticeQuestion;
import org.example.edusoft.learning.entity.Question;
import org.example.edusoft.learning.mapper.SelfPracticeMapper;
import org.example.edusoft.learning.mapper.SelfAnswerMapper;
import org.example.edusoft.learning.mapper.SelfSubmissionMapper;
import org.example.edusoft.learning.mapper.SelfPracticeQuestionMapper;
import org.example.edusoft.learning.mapper.QuestionMapper;
import org.example.edusoft.learning.service.SelfPracticeService;
import org.example.edusoft.learning.ai.AIServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class SelfPracticeServiceImpl implements SelfPracticeService {

    private static final Logger logger = LoggerFactory.getLogger(SelfPracticeServiceImpl.class);

    @Autowired
    private SelfPracticeMapper selfPracticeMapper;

    @Autowired
    private SelfPracticeQuestionMapper spqMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private SelfAnswerMapper answerMapper;

    @Autowired
    private SelfSubmissionMapper submissionMapper;

    @Autowired
    private AIServiceClient aiServiceClient;

    @Override
    @Transactional
    public Long saveGeneratedPractice(Long studentId, Map<String, Object> aiResult) {
        try {
            logger.info("开始保存AI生成的练习 - 用户ID: {}, AI结果: {}", studentId, aiResult);

            // 创建练习记录
            SelfPractice practice = new SelfPractice();
            practice.setStudentId(studentId);
            practice.setTitle("AI自测 " + LocalDateTime.now());
            practice.setCreatedAt(LocalDateTime.now());
            selfPracticeMapper.insert(practice);

            logger.info("练习记录已创建 - 练习ID: {}", practice.getId());

            // 兼容两种结构：直接包含 exercises，或包在 data.exercises
            List<Map<String, Object>> exercises = (List<Map<String, Object>>) aiResult.get("exercises");
            if (exercises == null && aiResult.get("data") instanceof Map) {
                exercises = (List<Map<String, Object>>) ((Map<?, ?>) aiResult.get("data")).get("exercises");
            }
            if (exercises == null) {
                logger.warn("AI自测练习创建警告 - 未找到练习题数据, 练习ID: {}", practice.getId());
                return practice.getId();
            }

            logger.info("开始保存AI自测练习题目 - 练习ID: {}, 题目数量: {}", practice.getId(), exercises.size());
            int order = 1;
            for (Map<String, Object> ex : exercises) {
                Question question = new Question();
                question.setCreatorId(studentId);
                question.setType(Question.QuestionType.valueOf(ex.get("type").toString()));
                question.setContent(ex.get("question").toString());
                question.setAnswer(ex.get("answer").toString());
                question.setAnalysis(ex.getOrDefault("explanation", "").toString());
                if (ex.containsKey("options")) {
                    List<String> opts = (List<String>) ex.get("options");
                    question.setOptionsList(opts);
                }
                // 直接保存题目避免严格校验字段
                question.setCreatedAt(LocalDateTime.now());
                // 修复：学生自建题目，course_id设为0，section_id不设置
                question.setCourseId(0L);
                questionMapper.createQuestion(question);
                logger.debug("AI自测练习 - 保存题目 - 题目ID: {}, 练习ID: {}", question.getId(), practice.getId());

                // 将新题目的ID写回原始列表，便于前端提交作答时使用
                ex.put("id", question.getId());
                ex.put("score", 10);

                // 创建练习-问题关联
                SelfPracticeQuestion spq = new SelfPracticeQuestion();
                spq.setSelfPracticeId(practice.getId());
                spq.setQuestionId(question.getId());
                spq.setSortOrder(order++);
                spq.setScore(10);
                spqMapper.insert(spq);
            }

            logger.info("AI自测练习创建完成 - ID: {}, 总题目数: {}", practice.getId(), exercises.size());
            return practice.getId();

        } catch (Exception e) {
            logger.error("保存AI生成的练习时出错 - 用户ID: {}", studentId, e);
            throw new RuntimeException("保存练习失败", e);
        }
    }

    @Override
    public boolean checkPracticeExists(Long practiceId) {
        if (practiceId == null || practiceId <= 0) {
            logger.warn("检查练习是否存在 - 无效的练习ID: {}", practiceId);
            return false;
        }
        
        SelfPractice practice = selfPracticeMapper.selectById(practiceId);
        boolean exists = practice != null;
        logger.info("检查练习是否存在 - 练习ID: {}, 结果: {}", practiceId, exists);
        return exists;
    }

    @Override
    public List<Map<String, Object>> getHistory(Long stuId) {
        // 查询学生所有提交记录，按提交时间倒序
        List<SelfSubmission> subs = submissionMapper.selectList(
                new LambdaQueryWrapper<SelfSubmission>()
                        .eq(SelfSubmission::getStudentId, stuId)
                        .orderByDesc(SelfSubmission::getSubmittedAt)
        );
        if (subs == null || subs.isEmpty()) return java.util.Collections.emptyList();
        
        List<Map<String, Object>> list = new ArrayList<>();
        for (SelfSubmission sub : subs) {
            SelfPractice sp = selfPracticeMapper.selectById(sub.getSelfPracticeId());
            Map<String, Object> item = new HashMap<>();
            item.put("practiceId", sub.getSelfPracticeId());
            item.put("title", sp != null ? sp.getTitle() : "AI自测");
            item.put("submittedAt", sub.getSubmittedAt());
            item.put("score", sub.getScore());
            list.add(item);
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> getDetail(Long stuId, Long practiceId) {
        // 获取学生在该练习的最新一次提交
        SelfSubmission sub = submissionMapper.selectOne(
                new LambdaQueryWrapper<SelfSubmission>()
                        .eq(SelfSubmission::getStudentId, stuId)
                        .eq(SelfSubmission::getSelfPracticeId, practiceId)
                        .orderByDesc(SelfSubmission::getSubmittedAt)
                        .last("limit 1")
        );
        if (sub == null) return java.util.Collections.emptyList();
        
        List<SelfAnswer> answers = answerMapper.selectList(
                new LambdaQueryWrapper<SelfAnswer>()
                        .eq(SelfAnswer::getSubmissionId, sub.getId())
                        .orderByAsc(SelfAnswer::getSortOrder)
        );
        
        List<Map<String, Object>> result = new ArrayList<>();
        int idx = 1;
        for (SelfAnswer ans : answers) {
            Question q = questionMapper.findById(ans.getQuestionId());
            Map<String, Object> map = new HashMap<>();
            map.put("order", idx++);
            map.put("questionId", ans.getQuestionId());
            map.put("question", q != null ? q.getContent() : "");
            map.put("options", q != null ? q.getOptionsList() : null);
            map.put("correctAnswer", q != null ? q.getAnswer() : "");
            map.put("studentAnswer", ans.getAnswerText());
            map.put("score", ans.getScore());
            map.put("correct", ans.getCorrect());
            result.add(map);
        }
        return result;
    }
}
