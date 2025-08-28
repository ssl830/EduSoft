package org.example.edusoft.service.practice;

import org.example.edusoft.common.domain.Result;
import org.example.edusoft.dto.practice.JudgeSubmissionRequest;
import org.example.edusoft.dto.practice.JudgeQuestionRequest;
import org.example.edusoft.dto.practice.PendingSubmissionDTO;
import org.example.edusoft.dto.practice.SubmissionDetailDTO;
import org.example.edusoft.entity.practice.*;
import org.example.edusoft.entity.user.User;
import org.example.edusoft.mapper.practice.*;
import org.example.edusoft.mapper.user.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManualJudgeServiceTest {

    @Mock
    private AnswerMapper answerMapper;
    @Mock
    private SubmissionMapper submissionMapper;
    @Mock
    private PracticeQuestionMapper practiceQuestionMapper;
    @Mock
    private QuestionMapper questionMapper;
    @Mock
    private PracticeMapper practiceMapper;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ManualJudgeService manualJudgeService;

    @Test
    @DisplayName("getPendingSubmissionList - 指定练习有待批改提交")
    void testGetPendingSubmissionList_practiceId_nonEmpty() {
        Submission sub = new Submission();
        sub.setPracticeId(1L);
        sub.setStudentId(2L);
        sub.setId(3L);
        when(submissionMapper.findByPracticeIdWithUnjudgedAnswers(1L)).thenReturn(Arrays.asList(sub));
        Practice practice = new Practice();
        practice.setTitle("练习1");
        when(practiceMapper.findById(1L)).thenReturn(practice);
        User user = new User();
        user.setUsername("张三");
        when(userMapper.findById(2L)).thenReturn(user);

        Result<List<PendingSubmissionDTO>> result = manualJudgeService.getPendingSubmissionList(1L, null);

        assertEquals(1, result.getData().size());
        assertEquals("张三", result.getData().get(0).getStudentName());
        assertEquals("练习1", result.getData().get(0).getPracticeName());
    }

    @Test
    @DisplayName("getPendingSubmissionList - 指定练习无待批改提交")
    void testGetPendingSubmissionList_practiceId_empty() {
        when(submissionMapper.findByPracticeIdWithUnjudgedAnswers(Long.valueOf(1))).thenReturn(Collections.emptyList());

        Result<List<PendingSubmissionDTO>> result = manualJudgeService.getPendingSubmissionList(Long.valueOf(1), null);

        assertTrue(result.getData().isEmpty());
    }

    @Test
    @DisplayName("getPendingSubmissionList - 班级有待批改提交")
    void testGetPendingSubmissionList_classId_nonEmpty() {
        Practice practice = new Practice();
        practice.setId(10L);
        practice.setTitle("练习2");
        when(practiceMapper.findByClassId(5L)).thenReturn(Arrays.asList(practice));
        Submission sub = new Submission();
        sub.setPracticeId(10L);
        sub.setStudentId(20L);
        sub.setId(30L);
        when(submissionMapper.findByPracticeIdWithUnjudgedAnswers(10L)).thenReturn(Arrays.asList(sub));
        when(practiceMapper.findById(10L)).thenReturn(practice);
        User user = new User();
        user.setUsername("李四");
        when(userMapper.findById(20L)).thenReturn(user);

        Result<List<PendingSubmissionDTO>> result = manualJudgeService.getPendingSubmissionList(null, 5L);

        assertEquals(1, result.getData().size());
        assertEquals("李四", result.getData().get(0).getStudentName());
        assertEquals("练习2", result.getData().get(0).getPracticeName());
    }

    @Test
    @DisplayName("getPendingSubmissionList - 班级无待批改提交")
    void testGetPendingSubmissionList_classId_empty() {
        when(practiceMapper.findByClassId(Long.valueOf(5))).thenReturn(Collections.emptyList());
        Result<List<PendingSubmissionDTO>> result = manualJudgeService.getPendingSubmissionList(null, Long.valueOf(5));
        assertTrue(result.getData().isEmpty());
    }

    @Test
    @DisplayName("getSubmissionDetail - 正常获取详情")
    void testGetSubmissionDetail_success() {
        Submission sub = new Submission();
        sub.setId(1L);
        sub.setStudentId(2L);
        sub.setPracticeId(3L);
        when(submissionMapper.selectById(1L)).thenReturn(sub);
        User user = new User();
        user.setUsername("王五");
        when(userMapper.findById(2L)).thenReturn(user);

        Answer answer = new Answer();
        answer.setQuestionId(4L);
        answer.setAnswerText("答题内容");
        answer.setSortOrder(1L);
        List<Answer> answers = Arrays.asList(answer);
        when(answerMapper.findBySubmissionId(1L)).thenReturn(answers);

        Question question = new Question();
        question.setId(4L);
        question.setContent("题目内容");
        question.setType(Question.QuestionType.program);
        when(questionMapper.selectById(4L)).thenReturn(question);

        PracticeQuestion pq = new PracticeQuestion();
        pq.setScore(10);
        when(practiceQuestionMapper.findByPracticeIdAndQuestionId(3L, 4L)).thenReturn(pq);

        Result<List<SubmissionDetailDTO>> result = manualJudgeService.getSubmissionDetail(1L);

        assertEquals(1, result.getData().size());
        assertEquals("题目内容", result.getData().get(0).getQuestionName());
        assertEquals("答题内容", result.getData().get(0).getAnswerText());
        assertEquals(10, result.getData().get(0).getMaxScore());
        assertEquals("王五", result.getData().get(0).getStudentName());
    }

    @Test
    @DisplayName("getSubmissionDetail - 提交不存在")
    void testGetSubmissionDetail_notFound() {
        when(submissionMapper.selectById(Long.valueOf(99))).thenReturn(null);

        Result<List<SubmissionDetailDTO>> result = manualJudgeService.getSubmissionDetail(Long.valueOf(99));

        assertEquals("提交不存在", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("getSubmissionDetail - 无非主观题答案")
    void testGetSubmissionDetail_noEssayAnswers() {
        Submission sub = new Submission();
        sub.setId(1L);
        sub.setStudentId(2L);
        sub.setPracticeId(3L);
        when(submissionMapper.selectById(1L)).thenReturn(sub);
        User user = new User();
        user.setUsername("王五");
        when(userMapper.findById(2L)).thenReturn(user);

        Answer answer = new Answer();
        answer.setQuestionId(4L);
        answer.setAnswerText("答题内容");
        answer.setSortOrder(1L);
        List<Answer> answers = Arrays.asList(answer);
        when(answerMapper.findBySubmissionId(1L)).thenReturn(answers);

        Question question = new Question();
        question.setId(4L);
        question.setContent("题目内容");
        question.setType(Question.QuestionType.singlechoice); // 非主观题
        when(questionMapper.selectById(4L)).thenReturn(question);

        Result<List<SubmissionDetailDTO>> result = manualJudgeService.getSubmissionDetail(1L);

        assertEquals(0, result.getData().size());
    }

    @Test
    @DisplayName("judgeSubmission - 正常批改")
    void testJudgeSubmission_success() {
        Submission sub = new Submission();
        sub.setId(1L);
        sub.setScore(5);
        when(submissionMapper.selectById(1L)).thenReturn(sub);

        JudgeQuestionRequest qReq = new JudgeQuestionRequest();
        qReq.setSortOrder(1L);
        qReq.setScore(10);
        qReq.setMaxScore(10);
        JudgeSubmissionRequest req = new JudgeSubmissionRequest();
        req.setSubmissionId(1L);
        req.setQuestions(Arrays.asList(qReq));

        Answer answer = new Answer();
        answer.setIsJudged(false);
        when(answerMapper.findBySubmissionIdAndSortOrder(1L, 1L)).thenReturn(answer);

        Result<Void> result = manualJudgeService.judgeSubmission(req);

        assertEquals("批改成功", result.getMsg());
        verify(answerMapper, times(1)).updateScoreAndJudgment(any(Answer.class));
        verify(submissionMapper, times(1)).update(any(Submission.class));
        assertTrue(answer.getCorrect());
        assertTrue(answer.getIsJudged());
    }

    @Test
    @DisplayName("judgeSubmission - 提交不存在")
    void testJudgeSubmission_notFound() {
        JudgeSubmissionRequest req = new JudgeSubmissionRequest();
        req.setSubmissionId(Long.valueOf(99));
        req.setQuestions(Collections.emptyList());
        when(submissionMapper.selectById(Long.valueOf(99))).thenReturn(null);

        Result<Void> result = manualJudgeService.judgeSubmission(req);

        assertEquals("提交不存在", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("judgeSubmission - 答案已批改")
    void testJudgeSubmission_answerAlreadyJudged() {
        Submission sub = new Submission();
        sub.setId(1L);
        sub.setScore(5);
        when(submissionMapper.selectById(1L)).thenReturn(sub);

        JudgeQuestionRequest qReq = new JudgeQuestionRequest();
        qReq.setSortOrder(1L);
        qReq.setScore(10);
        qReq.setMaxScore(10);
        JudgeSubmissionRequest req = new JudgeSubmissionRequest();
        req.setSubmissionId(1L);
        req.setQuestions(Arrays.asList(qReq));

        Answer answer = new Answer();
        answer.setIsJudged(true);
        when(answerMapper.findBySubmissionIdAndSortOrder(1L, 1L)).thenReturn(answer);

        Result<Void> result = manualJudgeService.judgeSubmission(req);

        assertEquals("批改成功", result.getMsg());
        verify(answerMapper, never()).updateScoreAndJudgment(any(Answer.class));
        verify(submissionMapper, times(1)).update(any(Submission.class));
        assertTrue(answer.getIsJudged());
    }
}
