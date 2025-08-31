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
    public void saveGeneratedPractice(String prompt, String result, Long studentId) {
        try {
            logger.info("开始保存AI生成的练习 - 用户ID: {}, prompt: {}", studentId, prompt);

            if (result == null || result.trim().isEmpty()) {
                logger.warn("AI返回结果为空 - 用户ID: {}", studentId);
                return;
            }

            // 解析AI结果
            String[] parts = result.split("\\[\\[QUESTION\\]\\]");
            if (parts.length < 2) {
                logger.warn("AI结果格式不正确，没有找到问题分隔符 - 用户ID: {}", studentId);
                return;
            }

            // 创建练习记录
            SelfPractice practice = new SelfPractice();
            practice.setStudentId(studentId);
            practice.setTitle("AI自测练习");
            practice.setPrompt(prompt);
            practice.setCreatedAt(LocalDateTime.now());
            selfPracticeMapper.insert(practice);

            logger.info("练习记录已创建 - 练习ID: {}", practice.getId());

            // 解析并保存每个问题
            for (int i = 1; i < parts.length; i++) {
                String questionBlock = parts[i].trim();
                if (questionBlock.isEmpty()) continue;

                try {
                    Question question = parseQuestionFromAI(questionBlock);
                    if (question != null) {
                        question.setCreatorId(studentId);
                        // 确保设置必要的字段以通过验证
                        if (question.getCourseId() == null) {
                            question.setCourseId(0L); // 默认课程ID，表示AI生成的题目
                        }
                        questionMapper.createQuestion(question);

                        // 创建练习-问题关联
                        SelfPracticeQuestion spq = new SelfPracticeQuestion();
                        spq.setSelfPracticeId(practice.getId());
                        spq.setQuestionId(question.getId());
                        spq.setSortOrder(i);
                        spqMapper.insert(spq);

                        logger.debug("问题已保存 - 问题ID: {}, 练习ID: {}", question.getId(), practice.getId());
                    }
                } catch (Exception e) {
                    logger.error("解析问题时出错 - 练习ID: {}, 问题块: {}", practice.getId(), questionBlock, e);
                }
            }

            logger.info("AI生成的练习保存完成 - 练习ID: {}", practice.getId());

        } catch (Exception e) {
            logger.error("保存AI生成的练习时出错 - 用户ID: {}", studentId, e);
            throw new RuntimeException("保存练习失败", e);
        }
    }

    private Question parseQuestionFromAI(String questionBlock) {
        try {
            // 解析问题格式：题目\n选项\n答案\n解析
            String[] lines = questionBlock.split("\\n");
            if (lines.length < 3) {
                logger.warn("问题格式不完整: {}", questionBlock);
                return null;
            }

            Question question = new Question();
            
            // 解析题目
            String content = lines[0].trim();
            if (content.startsWith("题目:") || content.startsWith("问题:")) {
                content = content.substring(3).trim();
            }
            question.setContent(content);

            // 解析选项
            StringBuilder optionsBuilder = new StringBuilder();
            String answer = "";
            String analysis = "";
            
            boolean isOptions = false;
            boolean isAnswer = false;
            boolean isAnalysis = false;

            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                
                if (line.startsWith("选项:") || line.startsWith("A.") || line.startsWith("A)")) {
                    isOptions = true;
                    isAnswer = false;
                    isAnalysis = false;
                    if (line.startsWith("选项:")) {
                        continue;
                    }
                } else if (line.startsWith("答案:") || line.startsWith("正确答案:")) {
                    isOptions = false;
                    isAnswer = true;
                    isAnalysis = false;
                    answer = line.substring(line.indexOf(":") + 1).trim();
                    continue;
                } else if (line.startsWith("解析:") || line.startsWith("分析:")) {
                    isOptions = false;
                    isAnswer = false;
                    isAnalysis = true;
                    analysis = line.substring(line.indexOf(":") + 1).trim();
                    continue;
                }

                if (isOptions && !line.isEmpty()) {
                    if (optionsBuilder.length() > 0) {
                        optionsBuilder.append("\n");
                    }
                    optionsBuilder.append(line);
                } else if (isAnswer && !line.isEmpty()) {
                    answer = line;
                } else if (isAnalysis && !line.isEmpty()) {
                    if (analysis.length() > 0) {
                        analysis += "\n";
                    }
                    analysis += line;
                }
            }

            question.setOptions(optionsBuilder.toString());
            question.setAnswer(answer);
            question.setAnalysis(analysis);
            question.setType(Question.QuestionType.singlechoice); // 设置为枚举类型
            question.setCreatedAt(LocalDateTime.now());

            return question;

        } catch (Exception e) {
            logger.error("解析问题时出错: {}", questionBlock, e);
            return null;
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
