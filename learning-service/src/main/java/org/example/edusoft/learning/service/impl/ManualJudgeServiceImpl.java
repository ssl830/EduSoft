package org.example.edusoft.learning.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.edusoft.learning.Result;
import org.example.edusoft.learning.client.UserClient;
import org.example.edusoft.learning.dto.JudgeQuestionRequest;
import org.example.edusoft.learning.dto.JudgeSubmissionRequest;
import org.example.edusoft.learning.dto.PendingSubmissionDTO;
import org.example.edusoft.learning.dto.SubmissionDetailDTO;
import org.example.edusoft.learning.entity.*;
import org.example.edusoft.learning.mapper.*;
import org.example.edusoft.learning.service.ManualJudgeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManualJudgeServiceImpl implements ManualJudgeService {

    private final AnswerMapper answerMapper;
    private final SubmissionMapper submissionMapper;
    private final QuestionMapper questionMapper;
    private final PracticeMapper practiceMapper;
    private final UserClient userClient; // 使用RestTemplate实现

    // 可根据实际情况注入baseUrl和token
    private final String userServiceBaseUrl = "http://localhost:8081"; // 示例

    @Override
    public Result<List<PendingSubmissionDTO>> getPendingSubmissionList(Long practiceId, Long classId) {
        List<PracticeSubmission> submissions;
        if (practiceId != null) {
            submissions = submissionMapper.findByPracticeIdWithUnjudgedAnswers(practiceId);
        } else {
            List<Practice> practices = practiceMapper.getPracticeList(classId);
            submissions = new ArrayList<>();
            for (Practice practice : practices) {
                submissions.addAll(submissionMapper.findByPracticeIdWithUnjudgedAnswers(practice.getId()));
            }
        }

        List<PendingSubmissionDTO> result = submissions.stream().map(submission -> {
            Practice practice = practiceMapper.getPracticeById(submission.getPracticeId());
            String studentName = "Unknown";
            try {
                // 这里token可从上下文获取，如SecurityContextHolder等
                String token = null; // TODO: 获取当前请求token
                Map<String, Object> userMap = userClient.fetchUserById(userServiceBaseUrl, token, submission.getStudentId());
                if (userMap != null && userMap.get("username") != null) {
                    studentName = userMap.get("username").toString();
                }
            } catch (Exception e) {
                // Log the exception, but continue with a placeholder name
            }
            return new PendingSubmissionDTO(
                studentName,
                practice.getTitle(),
                submission.getId()
            );
        }).collect(Collectors.toList());

        return Result.success(result, "获取成功");
    }

    @Override
    public Result<List<SubmissionDetailDTO>> getSubmissionDetail(Long submissionId) {
        PracticeSubmission submission = submissionMapper.selectById(submissionId);
        if (submission == null) {
            return Result.error("提交不存在");
        }

        // String studentName = userClient.getUserById(submission.getStudentId()).getData().getUsername();
        String studentName = "Unknown"; // Placeholder
        try {
            String token = null; // TODO: 获取当前请求token
            Map<String, Object> userMap = userClient.fetchUserById(userServiceBaseUrl, token, submission.getStudentId());
            if (userMap != null && userMap.get("username") != null) {
                studentName = userMap.get("username").toString();
            }
        } catch (Exception e) {
            // Log the exception, but continue with a placeholder name
        }

        List<Answer> answers = answerMapper.findBySubmissionId(submissionId);
        List<SubmissionDetailDTO> result = new ArrayList<>();

        for (Answer answer : answers) {
            Question question = questionMapper.selectById(answer.getQuestionId());
            if (question.getType() != Question.QuestionType.singlechoice && question.getType() != Question.QuestionType.judge
                    && question.getType() != Question.QuestionType.fillblank) {
                result.add(new SubmissionDetailDTO(
                        question.getContent(),
                        answer.getAnswer(),
                        question.getScore(),
                        studentName,
                        answer.getSortOrder()
                ));
            }
        }
        return Result.success(result, "获取成功");
    }

    @Override
    @Transactional
    public Result<Void> judgeSubmission(JudgeSubmissionRequest request) {
        PracticeSubmission submission = submissionMapper.selectById(request.getSubmissionId());
        if (submission == null) {
            return Result.error("提交不存在");
        }

        int totalScore = submission.getScore() != null ? submission.getScore() : 0;
        boolean allJudged = true;

        for (JudgeQuestionRequest judge : request.getQuestions()) {
            Answer answer = answerMapper.findBySubmissionIdAndSortOrder(request.getSubmissionId(), judge.getSortOrder().longValue());
            if (answer != null) {
                answer.setScore(judge.getScore());
                answer.setCorrect(judge.getScore() > 0); // Simple logic
                answer.setIsJudged(true);
                answerMapper.update(answer);
                totalScore += judge.getScore();
            }
        }

        List<Answer> allAnswers = answerMapper.findBySubmissionId(request.getSubmissionId());
        for (Answer ans : allAnswers) {
            if (!ans.getIsJudged()) {
                allJudged = false;
                break;
            }
        }

        submission.setScore(totalScore);
        if (allJudged) {
            submission.setIsJudged(1);
        }
        submissionMapper.update(submission);

        return Result.success(null, "批改成功");
    }
}
