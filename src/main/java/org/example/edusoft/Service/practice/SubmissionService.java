package org.example.edusoft.service.practice;

import org.example.edusoft.mapper.practice.*;
import org.example.edusoft.entity.practice.*;
import org.example.edusoft.common.domain.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

/**
 * 练习提交服务
 * 处理学生提交答案和自动评判的业务逻辑
 */
@Service
public class SubmissionService {

    @Autowired
    private PracticeQuestionMapper practiceQuestionMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private SubmissionMapper submissionMapper;

    /**
     * 提交练习答案并进行自动评判
     * @param practiceId 练习ID
     * @param studentId 学生ID
     * @param answers 答案列表，按题目顺序排列
     * @return 提交记录ID
     */
    @Transactional
    public Result<Long> submitAndAutoJudge(Long practiceId, Long studentId, List<String> answers) {
        // 1. 获取练习中的所有题目，按sort_order排序
        List<PracticeQuestion> practiceQuestions = practiceQuestionMapper.findpqByPracticeIdOrdered(practiceId);
        if (practiceQuestions.isEmpty()) {
            return Result.error("练习不存在或没有题目");
        }

        // 2. 创建提交记录
        Submission submission = new Submission();
        submission.setPracticeId(practiceId);
        submission.setStudentId(studentId);
        submission.setIsJudged(0);
        submission.setScore(0);
        submissionMapper.insert(submission);

        // 3. 处理每道题的答案
        int totalScore = 0;
        int nonSingleChoiceCount = 0;

        for (int i = 0; i < practiceQuestions.size(); i++) {
            PracticeQuestion pq = practiceQuestions.get(i);
            String userAnswer = i < answers.size() ? answers.get(i) : "";
            
            // 获取题目信息
            Question question = questionMapper.selectById(pq.getQuestionId());
            
            // 创建答案记录
            Answer answer = new Answer();
            answer.setSubmissionId(submission.getId());
            answer.setQuestionId(pq.getQuestionId());
            answer.setAnswerText(userAnswer);
            // 使用题目在练习中的排序作为答案的排序
            answer.setSortOrder(pq.getSortOrder() != null ? pq.getSortOrder() : Long.valueOf(i));

            // 根据题目类型处理
            if (question.getType() == Question.QuestionType.singlechoice
            || question.getType() == Question.QuestionType.judge
            || question.getType() == Question.QuestionType.fillblank
            || question.getType() == Question.QuestionType.multiplechoice) {
                // 单选题自动评判
                boolean isCorrect = userAnswer.equals(question.getAnswer());
                answer.setCorrect(isCorrect);
                answer.setScore(isCorrect ? pq.getScore() : 0);
                answer.setIsJudged(true);
                totalScore += answer.getScore();
            } else {
                // 非单选题，等待教师评分
                answer.setScore(0);
                answer.setCorrect(false);
                answer.setIsJudged(false);
                nonSingleChoiceCount++;
            }

            answerMapper.insert(answer);
        }

        // 4. 更新提交记录
        submission.setScore(totalScore);
        if(nonSingleChoiceCount > 0) {
            submission.setIsJudged(0);  
        } else {
            submission.setIsJudged(1);
        }
        submissionMapper.update(submission);

        return Result.success(submission.getId(), "提交成功");
    }
}
