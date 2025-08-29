package org.example.edusoft.service.practice;

import org.example.edusoft.entity.practice.Question;
import org.example.edusoft.entity.practice.QuestionListDTO;
import org.example.edusoft.exception.practice.PracticeException;
import org.example.edusoft.mapper.practice.QuestionMapper;
import org.example.edusoft.service.practice.impl.QuestionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuestionServiceTest {

    @InjectMocks
    private QuestionServiceImpl questionService;

    @Mock
    private QuestionMapper questionMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // createQuestion 正例
    @Test
    void testCreateQuestionSuccess() {
        Question q = new Question();
        q.setContent("题目内容");
        q.setType(Question.QuestionType.singlechoice);
        q.setAnswer("A");
        q.setCreatorId(1L);
        q.setCourseId(1L);
        q.setSectionId(1L);
        q.setOptions(String.valueOf(Arrays.asList("A", "B", "C")));
        doNothing().when(questionMapper).createQuestion(any());
        Question result = questionService.createQuestion(q);
        assertEquals("题目内容", result.getContent());
    }

    // createQuestion 反例：内容为空
    @Test
    void testCreateQuestionContentRequired() {
        Question q = new Question();
        q.setType(Question.QuestionType.singlechoice);
        q.setAnswer("A");
        q.setCreatorId(1L);
        q.setCourseId(1L);
        q.setSectionId(1L);
        q.setOptions(String.valueOf(Arrays.asList("A", "B", "C")));
        PracticeException ex = assertThrows(PracticeException.class, () -> questionService.createQuestion(q));
        assertEquals("QUESTION_CONTENT_REQUIRED", ex.getCode());
    }

    // updateQuestion 正例
    @Test
    void testUpdateQuestionSuccess() {
        Question q = new Question();
        q.setId(1L);
        q.setContent("新内容");
        Question exist = new Question();
        exist.setId(1L);
        exist.setContent("旧内容");
        when(questionMapper.getQuestionById(1L)).thenReturn(exist);
        doNothing().when(questionMapper).updateQuestion(any());
        Question result = questionService.updateQuestion(q);
        assertEquals("新内容", result.getContent());
    }

    // updateQuestion 反例：题目不存在
    @Test
    void testUpdateQuestionNotFound() {
        Question q = new Question();
        q.setId(99L);
        when(questionMapper.getQuestionById(99L)).thenReturn(null);
        PracticeException ex = assertThrows(PracticeException.class, () -> questionService.updateQuestion(q));
        assertEquals("QUESTION_NOT_FOUND", ex.getCode());
    }

    // getQuestionList 正例
    @Test
    void testGetQuestionListSuccess() {
        List<Question> list = Arrays.asList(new Question(), new Question());
        when(questionMapper.getQuestionList(1L, 0, 2)).thenReturn(list);
        List<Question> result = questionService.getQuestionList(1L, 1, 2);
        assertEquals(2, result.size());
    }

    // getQuestionList 反例：页码非法
    @Test
    void testGetQuestionListInvalidPage() {
        PracticeException ex = assertThrows(PracticeException.class, () -> questionService.getQuestionList(1L, 0, 2));
        assertEquals("QUESTION_INVALID_PAGE", ex.getCode());
    }

    // getQuestionDetail 正例
    @Test
    void testGetQuestionDetailSuccess() {
        Question q = new Question();
        q.setId(1L);
        when(questionMapper.getQuestionById(1L)).thenReturn(q);
        Question result = questionService.getQuestionDetail(1L);
        assertEquals(1L, result.getId());
    }

    // getQuestionDetail 反例：题目不存在
    @Test
    void testGetQuestionDetailNotFound() {
        when(questionMapper.getQuestionById(2L)).thenReturn(null);
        PracticeException ex = assertThrows(PracticeException.class, () -> questionService.getQuestionDetail(2L));
        assertEquals("QUESTION_NOT_FOUND", ex.getCode());
    }

    // deleteQuestion 正例
    @Test
    void testDeleteQuestionSuccess() {
        Question q = new Question();
        q.setId(1L);
        when(questionMapper.getQuestionById(1L)).thenReturn(q);
        doNothing().when(questionMapper).deleteQuestion(1L);
        assertDoesNotThrow(() -> questionService.deleteQuestion(1L));
    }

    // deleteQuestion 反例：题目不存在
    @Test
    void testDeleteQuestionNotFound() {
        when(questionMapper.getQuestionById(2L)).thenReturn(null);
        PracticeException ex = assertThrows(PracticeException.class, () -> questionService.deleteQuestion(2L));
        assertEquals("QUESTION_NOT_FOUND", ex.getCode());
    }

    // importQuestionsToPractice 正例
    @Test
    void testImportQuestionsToPracticeSuccess() {
        List<Long> ids = Arrays.asList(1L, 2L);
        List<Integer> scores = Arrays.asList(5, 10);
        when(questionMapper.getQuestionById(1L)).thenReturn(new Question());
        when(questionMapper.getQuestionById(2L)).thenReturn(new Question());
        when(questionMapper.getQuestionsByPractice(1L)).thenReturn(new ArrayList<>());
        doNothing().when(questionMapper).addQuestionToPractice(anyLong(), anyLong(), anyInt());
        assertDoesNotThrow(() -> questionService.importQuestionsToPractice(1L, ids, scores));
    }

    // importQuestionsToPractice 反例：题目ID为空
    @Test
    void testImportQuestionsToPracticeIdsRequired() {
        PracticeException ex = assertThrows(PracticeException.class, () -> questionService.importQuestionsToPractice(1L, null, Arrays.asList(1)));
        assertEquals("QUESTION_IDS_REQUIRED", ex.getCode());
    }

    // getQuestionsBySection 正例
    @Test
    void testGetQuestionsBySectionSuccess() {
        List<Question> list = Arrays.asList(new Question());
        when(questionMapper.getQuestionsBySection(1L, 1L)).thenReturn(list);
        List<Question> result = questionService.getQuestionsBySection(1L, 1L);
        assertEquals(1, result.size());
    }

    // getQuestionsBySection 反例：课程ID为空
    @Test
    void testGetQuestionsBySectionCourseIdRequired() {
        PracticeException ex = assertThrows(PracticeException.class, () -> questionService.getQuestionsBySection(null, 1L));
        assertEquals("COURSE_ID_REQUIRED", ex.getCode());
    }

    // batchCreateQuestions 正例
    @Test
    void testBatchCreateQuestionsSuccess() {
        Question q = new Question();
        q.setContent("题目");
        q.setType(Question.QuestionType.singlechoice);
        q.setAnswer("A");
        q.setCreatorId(1L);
        q.setCourseId(1L);
        q.setSectionId(1L);
        q.setOptions(String.valueOf(Arrays.asList("A", "B")));
        List<Question> list = Arrays.asList(q);
        doNothing().when(questionMapper).createQuestion(any());
        List<Question> result = questionService.batchCreateQuestions(list);
        assertEquals(1, result.size());
    }

    // batchCreateQuestions 反例：列表为空
    @Test
    void testBatchCreateQuestionsRequired() {
        PracticeException ex = assertThrows(PracticeException.class, () -> questionService.batchCreateQuestions(null));
        assertEquals("QUESTIONS_REQUIRED", ex.getCode());
    }

    // getQuestionListByTeacherAndSection 正例
    @Test
    void testGetQuestionListByTeacherAndSectionSuccess() {
        List<Question> list = Arrays.asList(new Question());
        when(questionMapper.getQuestionsByTeacherAndSection(1L, 1L, 1L)).thenReturn(list);
        List<Question> result = questionService.getQuestionListByTeacherAndSection(1L, 1L, 1L);
        assertEquals(1, result.size());
    }

    // getQuestionListByTeacherAndSection 反例：教师ID为空
    @Test
    void testGetQuestionListByTeacherAndSectionTeacherIdRequired() {
        PracticeException ex = assertThrows(PracticeException.class, () -> questionService.getQuestionListByTeacherAndSection(null, 1L, 1L));
        assertEquals("TEACHER_ID_REQUIRED", ex.getCode());
    }

    // getQuestionListByCourse 正例
    @Test
    void testGetQuestionListByCourseSuccess() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1L);
        map.put("content", "题目");
        map.put("course_id", 1L);
        map.put("course_name", "课程");
        map.put("section_id", 1L);
        map.put("section_name", "章节");
        map.put("creator_id", 1L);
        map.put("type", "singlechoice");
        map.put("answer", "A");
        map.put("options", "A|||B");
        when(questionMapper.getQuestionListWithNames(1L)).thenReturn(Arrays.asList(map));
        List<QuestionListDTO> result = questionService.getQuestionListByCourse(1L);
        assertEquals(1, result.size());
    }

    // getQuestionListByCourse 反例：课程ID为空
    @Test
    void testGetQuestionListByCourseCourseIdRequired() {
        PracticeException ex = assertThrows(PracticeException.class, () -> questionService.getQuestionListByCourse(null));
        assertEquals("COURSE_ID_REQUIRED", ex.getCode());
    }

    // getAllQuestions 正例
    @Test
    void testGetAllQuestionsSuccess() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1L);
        map.put("content", "题目");
        map.put("course_id", 1L);
        map.put("course_name", "课程");
        map.put("section_id", 1L);
        map.put("section_name", "章节");
        map.put("creator_id", 1L);
        map.put("type", "singlechoice");
        map.put("answer", "A");
        map.put("options", "A|||B");
        when(questionMapper.getAllQuestionsWithNames()).thenReturn(Arrays.asList(map));
        List<QuestionListDTO> result = questionService.getAllQuestions();
        assertEquals(1, result.size());
    }

    // getAllQuestions 反例：返回空列表
    @Test
    void testGetAllQuestionsEmpty() {
        when(questionMapper.getAllQuestionsWithNames()).thenReturn(new ArrayList<>());
        List<QuestionListDTO> result = questionService.getAllQuestions();
        assertTrue(result.isEmpty());
    }
}

