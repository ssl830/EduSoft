package org.example.edusoft.service.selfpractice;

import org.example.edusoft.entity.selfpractice.SelfPractice;
import org.example.edusoft.entity.selfpractice.SelfSubmission;
import org.example.edusoft.entity.selfpractice.SelfAnswer;
import org.example.edusoft.entity.practice.Question;
import org.example.edusoft.mapper.selfpractice.SelfPracticeQuestionMapper;
import org.example.edusoft.mapper.practice.QuestionMapper;
import org.example.edusoft.mapper.selfpractice.SelfSubmissionMapper;
import org.example.edusoft.mapper.selfpractice.SelfAnswerMapper;
import org.example.edusoft.mapper.selfpractice.SelfPracticeMapper;
import org.example.edusoft.service.selfpractice.impl.SelfPracticeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SelfPracticeServiceTest {

    @Mock
    private SelfPracticeQuestionMapper spqMapper;
    @Mock
    private QuestionMapper questionMapper;
    @Mock
    private SelfSubmissionMapper submissionMapper;
    @Mock
    private SelfAnswerMapper answerMapper;
    @Mock
    private SelfPracticeMapper selfPracticeMapper;

    @InjectMocks
    private SelfPracticeServiceImpl selfPracticeService;

    @BeforeEach
    void setUp() throws Exception {
        // 通过反射设置基类的baseMapper
        Field baseMapperField = selfPracticeService.getClass().getSuperclass().getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(selfPracticeService, selfPracticeMapper);
    }

    @Test
    @DisplayName("saveGeneratedPractice - 正常保存")
    void testSaveGeneratedPractice_success() {
        Map<String, Object> aiResult = new HashMap<>();
        List<Map<String, Object>> exercises = new ArrayList<>();
        Map<String, Object> ex = new HashMap<>();
        ex.put("type", "singlechoice");
        ex.put("question", "题目内容");
        ex.put("answer", "A");
        ex.put("explanation", "解析");
        ex.put("options", Arrays.asList("A", "B", "C", "D"));
        exercises.add(ex);
        aiResult.put("exercises", exercises);

        // 模拟插入SelfPractice并设置ID
        doAnswer(invocation -> {
            SelfPractice practice = invocation.getArgument(0);
            practice.setId(100L); // 设置ID
            return 1;
        }).when(selfPracticeMapper).insert(any(SelfPractice.class));

        // 模拟QuestionMapper的行为
        doAnswer(invocation -> {
            Question question = invocation.getArgument(0);
            question.setId(123L);
            return 1;
        }).when(questionMapper).createQuestion(any(Question.class));

        when(spqMapper.insert(any())).thenReturn(1);

        Long id = selfPracticeService.saveGeneratedPractice(1L, aiResult);

        assertNotNull(id);
        assertEquals(100L, id); // 验证返回的ID
    }

    @Test
    @DisplayName("saveGeneratedPractice - aiResult无exercises")
    void testSaveGeneratedPractice_noExercises() {
        Map<String, Object> aiResult = new HashMap<>();

        // 模拟插入SelfPractice并设置ID
        doAnswer(invocation -> {
            SelfPractice practice = invocation.getArgument(0);
            practice.setId(101L); // 设置ID
            return 1;
        }).when(selfPracticeMapper).insert(any(SelfPractice.class));

        Long id = selfPracticeService.saveGeneratedPractice(1L, aiResult);

        assertNotNull(id);
        assertEquals(101L, id); // 验证返回的ID
    }

    @Test
    @DisplayName("saveGeneratedPractice - aiResult.data.exercises结构")
    void testSaveGeneratedPractice_dataExercises() {
        Map<String, Object> aiResult = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> exercises = new ArrayList<>();
        Map<String, Object> ex = new HashMap<>();
        ex.put("type", "singlechoice");
        ex.put("question", "题目内容");
        ex.put("answer", "A");
        ex.put("explanation", "解析");
        ex.put("options", Arrays.asList("A", "B", "C", "D"));
        exercises.add(ex);
        data.put("exercises", exercises);
        aiResult.put("data", data);

        // 模拟插入SelfPractice并设置ID
        doAnswer(invocation -> {
            SelfPractice practice = invocation.getArgument(0);
            practice.setId(102L); // 设置ID
            return 1;
        }).when(selfPracticeMapper).insert(any(SelfPractice.class));

        // 模拟QuestionMapper的行为
        doAnswer(invocation -> {
            Question question = invocation.getArgument(0);
            question.setId(123L);
            return 1;
        }).when(questionMapper).createQuestion(any(Question.class));

        when(spqMapper.insert(any())).thenReturn(1);

        Long id = selfPracticeService.saveGeneratedPractice(1L, aiResult);

        assertNotNull(id);
        assertEquals(102L, id); // 验证返回的ID
    }

    @Test
    @DisplayName("checkPracticeExists - 存在")
    void testCheckPracticeExists_exists() {
        SelfPractice practice = new SelfPractice();
        practice.setId(1L);
        when(selfPracticeMapper.selectById(anyLong())).thenReturn(practice);

        boolean result = selfPracticeService.checkPracticeExists(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("checkPracticeExists - 不存在")
    void testCheckPracticeExists_notExists() {
        when(selfPracticeMapper.selectById(anyLong())).thenReturn(null);

        boolean result = selfPracticeService.checkPracticeExists(2L);

        assertFalse(result);
    }

    @Test
    @DisplayName("checkPracticeExists - 传入null或<=0")
    void testCheckPracticeExists_invalidId() {
        boolean result1 = selfPracticeService.checkPracticeExists(null);
        boolean result2 = selfPracticeService.checkPracticeExists(0L);

        assertFalse(result1);
        assertFalse(result2);
    }

    @Test
    @DisplayName("getHistory - 有提交记录")
    void testGetHistory_hasRecords() {
        SelfSubmission sub = new SelfSubmission();
        sub.setId(1L);
        sub.setSelfPracticeId(10L);
        sub.setStudentId(2L);
        sub.setSubmittedAt(LocalDateTime.now());
        sub.setScore(100);
        List<SelfSubmission> subs = Arrays.asList(sub);

        when(submissionMapper.selectList(any())).thenReturn(subs);
        SelfPractice sp = new SelfPractice();
        sp.setId(10L);
        sp.setTitle("AI自测");
        when(selfPracticeMapper.selectById(anyLong())).thenReturn(sp);

        List<Map<String, Object>> result = selfPracticeService.getHistory(2L);

        assertEquals(1, result.size());
        assertEquals("AI自测", result.get(0).get("title"));
    }

    @Test
    @DisplayName("getHistory - 无提交记录")
    void testGetHistory_noRecords() {
        when(submissionMapper.selectList(any())).thenReturn(Collections.emptyList());

        List<Map<String, Object>> result = selfPracticeService.getHistory(2L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getDetail - 有答案记录")
    void testGetDetail_hasAnswers() {
        SelfSubmission sub = new SelfSubmission();
        sub.setId(1L);
        sub.setSelfPracticeId(10L);
        sub.setStudentId(2L);
        sub.setSubmittedAt(LocalDateTime.now());
        when(submissionMapper.selectOne(any())).thenReturn(sub);

        SelfAnswer ans = new SelfAnswer();
        ans.setId(1L);
        ans.setSubmissionId(1L);
        ans.setQuestionId(100L);
        ans.setAnswerText("A");
        ans.setScore(10);
        ans.setCorrect(true);
        ans.setSortOrder(1);
        List<SelfAnswer> answers = Arrays.asList(ans);
        when(answerMapper.selectList(any())).thenReturn(answers);

        Question q = new Question();
        q.setId(100L);
        q.setContent("题目内容");
        q.setOptionsList(Arrays.asList("A", "B", "C", "D"));
        q.setAnswer("A");
        when(questionMapper.findById(100L)).thenReturn(q);

        List<Map<String, Object>> result = selfPracticeService.getDetail(2L, 10L);

        assertEquals(1, result.size());
        assertEquals("题目内容", result.get(0).get("question"));
        assertEquals("A", result.get(0).get("studentAnswer"));
        assertEquals(true, result.get(0).get("correct"));
    }

    @Test
    @DisplayName("getDetail - 无提交记录")
    void testGetDetail_noSubmission() {
        when(submissionMapper.selectOne(any())).thenReturn(null);

        List<Map<String, Object>> result = selfPracticeService.getDetail(2L, 10L);

        assertTrue(result.isEmpty());
    }
}