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
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManualJudgeServiceImpl implements ManualJudgeService {

    private final AnswerMapper answerMapper;
    private final SubmissionMapper submissionMapper;
    private final QuestionMapper questionMapper;
    private final PracticeMapper practiceMapper;
    private final UserClient userClient; // 使用RestTemplate实现
    private final PracticeQuestionMapper practiceQuestionMapper;

    // 建议将baseUrl通过@Value注入
    @org.springframework.beans.factory.annotation.Value("${services.user.base-url:http://localhost:8081}")
    private String userServiceBaseUrl;

    private String resolveOutboundToken() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes servlet) {
            String satoken = servlet.getRequest().getHeader("satoken");
            if (satoken != null && !satoken.isEmpty())
                return satoken;
            String cookie = servlet.getRequest().getHeader("Cookie");
            if (cookie != null) {
                for (String part : cookie.split(";")) {
                    String p = part.trim();
                    if (p.startsWith("satoken="))
                        return p.substring("satoken=".length());
                }
            }
            String auth = servlet.getRequest().getHeader("Authorization");
            if (auth != null && !auth.isEmpty())
                return auth;
        }
        return null;
    }

    @Override
    public Result<List<PendingSubmissionDTO>> getPendingSubmissionList(Long practiceId, Long classId) {
        System.out.println("test");
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
                String token = resolveOutboundToken();
                Map<String, Object> userMap = userClient.fetchUserById(userServiceBaseUrl, token, String.valueOf(submission.getStudentId()));
                System.out.println("wronggggggggggggggggggggggggggggggggg");
                if (userMap != null && userMap.get("username") != null) {
                    Object name = userMap.get("username");
                    studentName = name == null ? null : String.valueOf(name);
                }
            } catch (Exception e) {
                System.out.println("wronggggggggggggggggggggggggggggggggggggggggggggggg");
                studentName = "异常:" + e.getMessage();
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

        String studentName = "Unknown";
        try {
            String token = resolveOutboundToken();
            Map<String, Object> userMap = userClient.fetchUserById(userServiceBaseUrl, token, String.valueOf(submission.getStudentId()));
            if (userMap != null && userMap.get("username") != null) {
                studentName = userMap.get("username").toString();
            }
        } catch (Exception e) {
            studentName = "异常:" + e.getMessage();
        }

        List<Answer> answers = answerMapper.findBySubmissionId(submissionId);
        List<SubmissionDetailDTO> result = new ArrayList<>();

        for (Answer answer : answers) {
            Question question = questionMapper.selectById(answer.getQuestionId());
            // 获取分值
            Integer maxScore = practiceQuestionMapper.getScoreByPracticeIdAndQuestionId(submission.getPracticeId(), answer.getQuestionId());
//            try {
//                maxScore = practiceQuestionMapper.getScoreByPracticeIdAndQuestionId(submission.getPracticeId(), question.getId());
//            } catch (Exception e) {
//                maxScore = question.getScore(); // 兜底
//            }
            if (question.getType() != Question.QuestionType.singlechoice && question.getType() != Question.QuestionType.judge
                    && question.getType() != Question.QuestionType.fillblank) {
                result.add(new SubmissionDetailDTO(
                        question.getContent(),
                        answer.getAnswer(),
                        maxScore,
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
                answer.setCorrect(Objects.equals(judge.getScore(), judge.getMaxScore())); // Simple logic
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
