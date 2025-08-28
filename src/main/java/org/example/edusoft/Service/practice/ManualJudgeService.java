package org.example.edusoft.service.practice;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.ArrayList;

import org.example.edusoft.entity.practice.*;
import org.example.edusoft.entity.user.User;
import org.example.edusoft.dto.practice.*;
import org.example.edusoft.common.domain.Result;
import org.example.edusoft.mapper.user.UserMapper;
import org.example.edusoft.mapper.practice.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.edusoft.dto.practice.JudgeQuestionRequest;

/**
 * 教师批改服务
 */
@Service
public class ManualJudgeService {

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private SubmissionMapper submissionMapper;

    @Autowired
    private PracticeQuestionMapper practiceQuestionMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private PracticeMapper practiceMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取待批改的提交列表
     */
    public Result<List<PendingSubmissionDTO>> getPendingSubmissionList(Long practiceId, Long classId) {
        System.out.println("practiceId: " + practiceId);
        System.out.println("classId: " + classId);
        List<Submission> submissions;
        if (practiceId != null) {
            // 获取指定练习的待批改提交
            submissions = submissionMapper.findByPracticeIdWithUnjudgedAnswers(practiceId);
        } else {
            // 获取班级下所有练习的待批改提交
            List<Practice> practices = practiceMapper.findByClassId(classId);
            System.out.println("practices: " + practices);
            System.out.println();
            submissions = new ArrayList<>();
            for (Practice practice : practices) {
                submissions.addAll(submissionMapper.findByPracticeIdWithUnjudgedAnswers(practice.getId()));
            }
        }

        // 转换为DTO
        List<PendingSubmissionDTO> result = submissions.stream().map(submission -> {
            Practice practice = practiceMapper.findById(submission.getPracticeId());
            String studentName = userMapper.findById(submission.getStudentId()).getUsername();
            
            return new PendingSubmissionDTO(
                studentName,
                practice.getTitle(),
                submission.getId()
            );
        }).collect(Collectors.toList());

        return Result.ok(result, "获取成功");
    }

    /**
     * 获取提交答案详情
     */
    public Result<List<SubmissionDetailDTO>> getSubmissionDetail(Long submissionId) {
        // 1. 获取提交信息
        Submission submission = submissionMapper.selectById(submissionId);
        if (submission == null) {
            return Result.error("提交不存在");
        }

        // 2. 获取学生信息
        String studentName = userMapper.findById(submission.getStudentId()).getUsername();

        // 3. 获取所有非单选题答案
        List<Answer> answers = answerMapper.findBySubmissionId(submissionId);
        List<SubmissionDetailDTO> result = new ArrayList<>();

        for (Answer answer : answers) {
            Question question = questionMapper.selectById(answer.getQuestionId());
            // 只返回非单选题
            if (question.getType() != Question.QuestionType.singlechoice && question.getType() != Question.QuestionType.judge 
            && question.getType() != Question.QuestionType.fillblank && question.getType() != Question.QuestionType.multiplechoice) {
                // 获取题目分值
                PracticeQuestion pq = practiceQuestionMapper.findByPracticeIdAndQuestionId(
                    submission.getPracticeId(), question.getId());
                
                result.add(new SubmissionDetailDTO(
                    question.getContent(),
                    answer.getAnswerText(),
                    pq.getScore(),
                    studentName,
                    answer.getSortOrder()
                ));
            }
        }

        // 按题目顺序排序
        result.sort((a, b) -> a.getSortOrder().compareTo(b.getSortOrder()));

        return Result.ok(result, "获取成功");
    }

    /**
     * 批改提交
     */
    @Transactional
    public Result<Void> judgeSubmission(JudgeSubmissionRequest request) {
        // 1. 获取提交信息
        System.out.println("request: " + request);
        System.out.println();
        Submission submission = submissionMapper.selectById(request.getSubmissionId());
        if (submission == null) {
            return Result.error("提交不存在");
        } 

        // 2. 更新每道题的得分
        int totalScore = submission.getScore(); // 保留单选题分数

        for (JudgeQuestionRequest questionRequest : request.getQuestions()) {
            // 根据提交ID和题目顺序找到答案
            Answer answer = answerMapper.findBySubmissionIdAndSortOrder(
                submission.getId(), questionRequest.getSortOrder());
            
            if (answer != null && !answer.getIsJudged()) {
                answer.setScore(questionRequest.getScore());
                answer.setIsJudged(true);
                // 如果得分等于最高分，设置correct为true
                answer.setCorrect(questionRequest.getScore() >= questionRequest.getMaxScore());
                answerMapper.updateScoreAndJudgment(answer);
                
                totalScore += questionRequest.getScore();
                //judgedCount++;
            }
        }

        // 3. 更新提交状态
        submission.setScore(totalScore);
        submission.setIsJudged(1);
        submissionMapper.update(submission);

        return Result.ok(null, "批改成功");
    } 
}