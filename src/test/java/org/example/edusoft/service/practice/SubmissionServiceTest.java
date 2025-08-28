package org.example.edusoft.service.practice;

import org.example.edusoft.entity.practice.*;
import org.example.edusoft.common.domain.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import org.example.edusoft.service.practice.SubmissionService;
import org.example.edusoft.mapper.practice.PracticeQuestionMapper;
import org.example.edusoft.mapper.practice.QuestionMapper;
import org.example.edusoft.mapper.practice.AnswerMapper;
import org.example.edusoft.mapper.practice.SubmissionMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubmissionServiceTest {

    @InjectMocks
    private SubmissionService submissionService;

    @Mock
    private PracticeQuestionMapper practiceQuestionMapper;
    @Mock
    private QuestionMapper questionMapper;
    @Mock
    private AnswerMapper answerMapper;
    @Mock
    private SubmissionMapper submissionMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSubmitAndAutoJudge_success() {
        // 构造练习题目
        PracticeQuestion pq = new PracticeQuestion();
        pq.setQuestionId(1L);
        pq.setScore(5);
        pq.setSortOrder(1L);
        List<PracticeQuestion> pqList = Arrays.asList(pq);

        when(practiceQuestionMapper.findpqByPracticeIdOrdered(100L)).thenReturn(pqList);

        Question q = new Question();
        q.setId(1L);
        q.setType(Question.QuestionType.singlechoice);
        q.setAnswer("A");
        when(questionMapper.selectById(1L)).thenReturn(q);

        // 提交记录插入模拟
        doAnswer(invocation -> {
            Submission sub = invocation.getArgument(0);
            sub.setId(10L);
            return null;
        }).when(submissionMapper).insert(any());

        doNothing().when(answerMapper).insert(any());
        doNothing().when(submissionMapper).update(any());

        List<String> answers = Arrays.asList("A");
        Result<Long> result = submissionService.submitAndAutoJudge(100L, 200L, answers);

        assertEquals(0, result.getCode());
        assertEquals(10L, result.getData());
        assertEquals("提交成功", result.getMsg());
    }

    @Test
    void testSubmitAndAutoJudge_noQuestions() {
        when(practiceQuestionMapper.findpqByPracticeIdOrdered(101L)).thenReturn(Collections.emptyList());

        Result<Long> result = submissionService.submitAndAutoJudge(101L, 201L, Arrays.asList("A"));

        assertNotEquals(0, result.getCode());
        assertEquals("练习不存在或没有题目", result.getMsg());
        assertNull(result.getData());
    }
}
