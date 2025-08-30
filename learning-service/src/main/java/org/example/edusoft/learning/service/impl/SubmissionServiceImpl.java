package org.example.edusoft.learning.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.edusoft.learning.Result;
import org.example.edusoft.learning.entity.Answer;
import org.example.edusoft.learning.entity.PracticeQuestion;
import org.example.edusoft.learning.entity.PracticeSubmission;
import org.example.edusoft.learning.entity.Question;
import org.example.edusoft.learning.mapper.AnswerMapper;
import org.example.edusoft.learning.mapper.PracticeMapper;
import org.example.edusoft.learning.mapper.QuestionMapper;
import org.example.edusoft.learning.mapper.SubmissionMapper;
import org.example.edusoft.learning.service.SubmissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final QuestionMapper questionMapper;
    private final AnswerMapper answerMapper;
    private final SubmissionMapper submissionMapper;
    private final PracticeMapper practiceMapper; // Assuming PracticeQuestionMapper is merged or methods are in PracticeMapper

    @Override
    @Transactional
    public Result<Long> submitAndAutoJudge(Long practiceId, Long studentId, List<String> answers) {
        // This method needs to be adapted as PracticeQuestionMapper is not available.
        // Assuming getQuestionsByPractice returns questions with their order.
        List<Question> questions = questionMapper.getQuestionsByPractice(practiceId);
        if (questions.isEmpty()) {
            return Result.error("练习不存在或没有题目");
        }

        PracticeSubmission submission = new PracticeSubmission();
        submission.setPracticeId(practiceId);
        submission.setStudentId(studentId);
        submission.setIsJudged(0);
        submission.setScore(0);
        submissionMapper.insert(submission);

        int totalScore = 0;
        int nonSingleChoiceCount = 0;

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            String userAnswer = i < answers.size() ? answers.get(i) : "";

            Answer answer = new Answer();
            answer.setSubmissionId(submission.getId());
            answer.setQuestionId(question.getId());
            answer.setAnswerText(userAnswer);
            answer.setSortOrder((long) i);

            if (question.getType() == Question.QuestionType.singlechoice
                    || question.getType() == Question.QuestionType.judge
                    || question.getType() == Question.QuestionType.fillblank) {
                boolean isCorrect = userAnswer.equalsIgnoreCase(question.getAnswer());
                answer.setCorrect(isCorrect);
                answer.setScore(isCorrect ? question.getScore() : 0);
                answer.setIsJudged(true);
                if (isCorrect) {
                    totalScore += question.getScore();
                }
            } else {
                answer.setScore(0);
                answer.setCorrect(false);
                answer.setIsJudged(false);
                nonSingleChoiceCount++;
            }

            answerMapper.insert(answer);
        }

        submission.setScore(totalScore);
        if (nonSingleChoiceCount > 0) {
            submission.setIsJudged(0);
        } else {
            submission.setIsJudged(1);
        }
        submissionMapper.update(submission);

        return Result.success(submission.getId(), "提交成功");
    }
}
