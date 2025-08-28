package org.example.edusoft.service.practice;

import org.example.edusoft.entity.practice.Practice;
import org.example.edusoft.entity.practice.Question;
import org.example.edusoft.entity.practice.PracticeListDTO;
import org.example.edusoft.entity.practice.PracticeQuestion;
import org.example.edusoft.entity.practice.Submission;
import org.example.edusoft.entity.practice.Answer;
import org.example.edusoft.exception.practice.PracticeException;
import org.example.edusoft.service.practice.impl.PracticeServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PracticeServiceTest {

    @Mock
    private org.example.edusoft.mapper.practice.PracticeMapper practiceMapper;
    @Mock
    private org.example.edusoft.mapper.practice.QuestionMapper questionMapper;
    @Mock
    private org.example.edusoft.utils.NotificationUtils notificationUtils;
    @Mock
    private org.example.edusoft.mapper.record.PracticeRecordMapper practiceRecordMapper;
    @Mock
    private org.example.edusoft.mapper.practice.SubmissionMapper submissionMapper;
    @Mock
    private org.example.edusoft.mapper.practice.AnswerMapper answerMapper;
    @Mock
    private org.example.edusoft.mapper.practice.PracticeQuestionMapper practiceQuestionMapper;

    @InjectMocks
    private PracticeServiceImpl practiceService;

    @Test
    @DisplayName("创建练习 - 正常创建")
    void testCreatePractice_success() {
        Practice practice = new Practice();
        practice.setTitle("练习1");
        practice.setCourseId(1L);
        practice.setClassId(2L);
        practice.setCreatedBy(3L);
        practice.setStartTime(LocalDateTime.now());
        practice.setEndTime(LocalDateTime.now().plusDays(1));
        when(practiceMapper.getClassStudentIds(2L)).thenReturn(Arrays.asList(10L, 11L));
        // 修改这里，兼容非void方法
        doAnswer(invocation -> null).when(notificationUtils).createPracticeNotification(any(), any(), any(), any(), any());
        doNothing().when(practiceMapper).createPractice(any(Practice.class));

        Practice result = practiceService.createPractice(practice);

        assertEquals("练习1", result.getTitle());
    }

    @Test
    @DisplayName("创建练习 - 开始时间晚于结束时间")
    void testCreatePractice_invalidTime() {
        Practice practice = new Practice();
        practice.setTitle("练习2");
        practice.setCourseId(1L);
        practice.setClassId(2L);
        practice.setCreatedBy(3L);
        practice.setStartTime(LocalDateTime.now().plusDays(2));
        practice.setEndTime(LocalDateTime.now().plusDays(1));

        PracticeException ex = assertThrows(PracticeException.class, () -> practiceService.createPractice(practice));
        assertEquals("PRACTICE_INVALID_TIME", ex.getCode());
    }

    @Test
    @DisplayName("更新练习 - 正常更新")
    void testUpdatePractice_success() {
        Practice practice = new Practice();
        practice.setId(1L);
        practice.setTitle("新标题"); // 修正为正常中文
        practice.setStartTime(LocalDateTime.now());
        practice.setEndTime(LocalDateTime.now().plusDays(1));
        Practice existing = new Practice();
        existing.setId(1L);
        when(practiceMapper.getPracticeById(1L)).thenReturn(existing);

        Practice result = practiceService.updatePractice(practice);

        assertEquals("新标题", result.getTitle());
    }

    @Test
    @DisplayName("更新练习 - 练习不存在")
    void testUpdatePractice_notFound() {
        Practice practice = new Practice();
        practice.setId(99L);
        when(practiceMapper.getPracticeById(99L)).thenReturn(null);

        PracticeException ex = assertThrows(PracticeException.class, () -> practiceService.updatePractice(practice));
        assertEquals("PRACTICE_NOT_FOUND", ex.getCode());
    }

    @Test
    @DisplayName("获取练习列表 - 正常获取")
    void testGetPracticeList_success() {
        List<Practice> list = Arrays.asList(new Practice(), new Practice());
        when(practiceMapper.getPracticeList(1L)).thenReturn(list);

        List<Practice> result = practiceService.getPracticeList(1L);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("获取练习列表 - 班级ID为空")
    void testGetPracticeList_nullClassId() {
        PracticeException ex = assertThrows(PracticeException.class, () -> practiceService.getPracticeList(null));
        assertEquals("PRACTICE_CLASS_REQUIRED", ex.getCode());
    }

    @Test
    @DisplayName("获取练习详情 - 正常获取")
    void testGetPracticeDetail_success() {
        Practice practice = new Practice();
        practice.setId(1L);
        when(practiceMapper.getPracticeById(1L)).thenReturn(practice);
        when(questionMapper.getQuestionsByPractice(1L)).thenReturn(Arrays.asList(new Question()));

        Practice result = practiceService.getPracticeDetail(1L);

        assertEquals(1L, result.getId());
        assertNotNull(result.getQuestions());
    }

    @Test
    @DisplayName("获取练习详情 - 练习不存在")
    void testGetPracticeDetail_notFound() {
        when(practiceMapper.getPracticeById(2L)).thenReturn(null);

        PracticeException ex = assertThrows(PracticeException.class, () -> practiceService.getPracticeDetail(2L));
        assertEquals("PRACTICE_NOT_FOUND", ex.getCode());
    }

    @Test
    @DisplayName("删除练习 - 正常删除")
    void testDeletePractice_success() {
        Practice practice = new Practice();
        practice.setId(1L);
        when(practiceMapper.getPracticeById(1L)).thenReturn(practice);
        when(submissionMapper.findSubmissionIdsByPracticeId(1L)).thenReturn(Arrays.asList(100L, 101L));
        doNothing().when(answerMapper).deleteAnswersBySubmissionIds(anyList());
        doNothing().when(questionMapper).removeAllQuestionsFromPractice(1L);
        doNothing().when(submissionMapper).removeSubmissionsByPracticeId(1L);
        doNothing().when(practiceMapper).deletePractice(1L);

        assertDoesNotThrow(() -> practiceService.deletePractice(1L));
    }

    @Test
    @DisplayName("删除练习 - 练习不存在")
    void testDeletePractice_notFound() {
        when(practiceMapper.getPracticeById(2L)).thenReturn(null);

        PracticeException ex = assertThrows(PracticeException.class, () -> practiceService.deletePractice(2L));
        assertEquals("PRACTICE_NOT_FOUND", ex.getCode());
    }

    @Test
    @DisplayName("添加题目到练习 - 正常添加")
    void testAddQuestionToPractice_success() {
        Practice practice = new Practice();
        practice.setId(1L);
        when(practiceMapper.getPracticeById(1L)).thenReturn(practice);
        Question question = new Question();
        question.setId(2L);
        when(questionMapper.getQuestionById(2L)).thenReturn(question);
        when(questionMapper.getQuestionsByPractice(1L)).thenReturn(Collections.emptyList());
        doNothing().when(questionMapper).addQuestionToPractice(1L, 2L, 5);

        assertDoesNotThrow(() -> practiceService.addQuestionToPractice(1L, 2L, 5));
    }

    @Test
    @DisplayName("添加题目到练习 - 题目已存在")
    void testAddQuestionToPractice_alreadyExists() {
        Practice practice = new Practice();
        practice.setId(1L);
        when(practiceMapper.getPracticeById(1L)).thenReturn(practice);
        Question question = new Question();
        question.setId(2L);
        when(questionMapper.getQuestionById(2L)).thenReturn(question);
        when(questionMapper.getQuestionsByPractice(1L)).thenReturn(Arrays.asList(question));

        PracticeException ex = assertThrows(PracticeException.class, () -> practiceService.addQuestionToPractice(1L, 2L, 5));
        assertEquals("QUESTION_ALREADY_EXISTS", ex.getCode());
    }

    @Test
    @DisplayName("移除题目 - 正常移除")
    void testRemoveQuestionFromPractice_success() {
        Practice practice = new Practice();
        practice.setId(1L);
        when(practiceMapper.getPracticeById(1L)).thenReturn(practice);
        doNothing().when(questionMapper).removeQuestionFromPractice(1L, 2L);

        assertDoesNotThrow(() -> practiceService.removeQuestionFromPractice(1L, 2L));
    }

    @Test
    @DisplayName("移除题目 - 练习不存在")
    void testRemoveQuestionFromPractice_notFound() {
        when(practiceMapper.getPracticeById(2L)).thenReturn(null);

        PracticeException ex = assertThrows(PracticeException.class, () -> practiceService.removeQuestionFromPractice(2L, 3L));
        assertEquals("PRACTICE_NOT_FOUND", ex.getCode());
    }

    @Test
    @DisplayName("获取练习题目列表 - 正常获取")
    void testGetPracticeQuestions_success() {
        Practice practice = new Practice();
        practice.setId(1L);
        when(practiceMapper.getPracticeById(1L)).thenReturn(practice);
        when(questionMapper.getQuestionsByPractice(1L)).thenReturn(Arrays.asList(new Question()));

        List<Question> result = practiceService.getPracticeQuestions(1L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("获取练习题目列表 - 练习不存在")
    void testGetPracticeQuestions_notFound() {
        when(practiceMapper.getPracticeById(2L)).thenReturn(null);

        PracticeException ex = assertThrows(PracticeException.class, () -> practiceService.getPracticeQuestions(2L));
        assertEquals("PRACTICE_NOT_FOUND", ex.getCode());
    }

    @Test
    @DisplayName("收藏题目 - 正常收藏")
    void testFavoriteQuestion_success() {
        when(practiceMapper.isQuestionFavorited(1L, 2L)).thenReturn(false);
        doNothing().when(practiceMapper).insertFavoriteQuestion(1L, 2L);

        assertDoesNotThrow(() -> practiceService.favoriteQuestion(1L, 2L));
    }
    @Test
    @DisplayName("收藏题目 - 已收藏")
    void testFavoriteQuestion_alreadyFavorited() {
        when(practiceMapper.isQuestionFavorited(1L, 2L)).thenReturn(true);

        assertDoesNotThrow(() -> practiceService.favoriteQuestion(1L, 2L));
        verify(practiceMapper, never()).insertFavoriteQuestion(anyLong(), anyLong());
    }

    @Test
    @DisplayName("取消收藏题目 - 正常取消")
    void testUnfavoriteQuestion_success() {
        doNothing().when(practiceMapper).deleteFavoriteQuestion(1L, 2L);

        assertDoesNotThrow(() -> practiceService.unfavoriteQuestion(1L, 2L));
    }

    @Test
    @DisplayName("获取收藏题目列表 - 正常获取")
    void testGetFavoriteQuestions_success() {
        List<Map<String, Object>> list = Arrays.asList(new HashMap<>());
        when(practiceMapper.findFavoriteQuestions(1L)).thenReturn(list);

        List<Map<String, Object>> result = practiceService.getFavoriteQuestions(1L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("添加错题 - 正常添加")
    void testAddWrongQuestion_success() {
        Question question = new Question();
        question.setId(2L);
        question.setAnswer("正确答案");
        when(questionMapper.findById(2L)).thenReturn(question);
        when(practiceMapper.existsWrongQuestion(1L, 2L)).thenReturn(false);
        doNothing().when(practiceMapper).insertWrongQuestion(1L, 2L, "错误答案", "正确答案");

        assertDoesNotThrow(() -> practiceService.addWrongQuestion(1L, 2L, "错误答案"));
    }

    @Test
    @DisplayName("添加错题 - 已存在错题")
    void testAddWrongQuestion_alreadyExists() {
        Question question = new Question();
        question.setId(2L);
        question.setAnswer("正确答案");
        when(questionMapper.findById(2L)).thenReturn(question);
        when(practiceMapper.existsWrongQuestion(1L, 2L)).thenReturn(true);
        doNothing().when(practiceMapper).updateWrongQuestion(1L, 2L, "错误答案", "正确答案");

        assertDoesNotThrow(() -> practiceService.addWrongQuestion(1L, 2L, "错误答案"));
    }

    @Test
    @DisplayName("获取错题列表 - 正常获取")
    void testGetWrongQuestions_success() {
        List<Map<String, Object>> list = Arrays.asList(new HashMap<>());
        when(practiceMapper.findWrongQuestions(1L)).thenReturn(list);

        List<Map<String, Object>> result = practiceService.getWrongQuestions(1L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("获取某课程错题列表 - 正常获取")
    void testGetWrongQuestionsByCourse_success() {
        List<Map<String, Object>> list = Arrays.asList(new HashMap<>());
        when(practiceMapper.findWrongQuestionsByCourse(1L, 2L)).thenReturn(list);

        List<Map<String, Object>> result = practiceService.getWrongQuestionsByCourse(1L, 2L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("删除错题 - 正常删除")
    void testRemoveWrongQuestion_success() {
        doNothing().when(practiceMapper).deleteWrongQuestion(1L, 2L);

        assertDoesNotThrow(() -> practiceService.removeWrongQuestion(1L, 2L));
    }

    @Test
    @DisplayName("获取课程练习 - 正常获取")
    void testGetCoursePractices_success() {
        when(practiceMapper.findClassIdByUserAndCourse(1L, 2L)).thenReturn(3L);
        List<Map<String, Object>> list = Arrays.asList(new HashMap<>());
        when(practiceMapper.findCoursePractices(2L, 3L, 1L)).thenReturn(list);

        List<Map<String, Object>> result = practiceService.getCoursePractices(1L, 2L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("获取课程练习 - 未找到班级")
    void testGetCoursePractices_noClass() {
        when(practiceMapper.findClassIdByUserAndCourse(1L, 2L)).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> practiceService.getCoursePractices(1L, 2L));
        assertEquals("未找到学生所在班级", ex.getMessage());
    }

    @Test
    @DisplayName("获取学生练习列表 - 正常获取")
    void testGetStudentPracticeList_success() {
        List<PracticeListDTO> list = Arrays.asList(new PracticeListDTO());
        when(practiceMapper.getStudentPracticeList(1L, 2L)).thenReturn(list);

        List<PracticeListDTO> result = practiceService.getStudentPracticeList(1L, 2L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("获取学生练习列表 - 参数为空")
    void testGetStudentPracticeList_nullParam() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> practiceService.getStudentPracticeList(null, null));
        assertEquals("学生ID和班级ID不能为空", ex.getMessage());
    }

    @Test
    @DisplayName("获取教师练习列表 - 正常获取")
    void testGetTeacherPractices_success() {
        List<Map<String, Object>> list = Arrays.asList(new HashMap<>());
        when(practiceMapper.getPracticesByTeacherId(1L)).thenReturn(list);

        List<Map<String, Object>> result = practiceService.getTeacherPractices(1L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("获取练习提交统计 - 正常获取")
    void testGetSubmissionStats_success() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("count", 10);
        when(practiceRecordMapper.getSubmissionStatsByPracticeId(1L)).thenReturn(stats);

        Map<String, Object> result = practiceService.getSubmissionStats(1L);

        assertEquals(10, result.get("count"));
    }

    @Test
    @DisplayName("updateScoreRateAfterDeadline - 正常统计")
    void testUpdateScoreRateAfterDeadline_success() {
        PracticeQuestion pq = new PracticeQuestion();
        pq.setQuestionId(1L);
        pq.setScore(10);
        when(practiceQuestionMapper.findpqByPracticeId(1L)).thenReturn(Arrays.asList(pq));
        Submission sub = new Submission();
        sub.setId(100L);
        when(submissionMapper.findByPracticeId(1L)).thenReturn(Arrays.asList(sub));
        Answer ans = new Answer();
        ans.setScore(8);
        when(answerMapper.findByQuestionIdsAndSubmissionId(anyList(), eq(100L))).thenReturn(Arrays.asList(ans));
        // 只mock void方法
        when(practiceQuestionMapper.updateScoreRate(anyLong(), anyLong(), anyDouble())).thenReturn(1);
        assertDoesNotThrow(() -> practiceService.updateScoreRateAfterDeadline(1L));
    }

    @Test
    @DisplayName("updateScoreRateAfterDeadline - 无题目或无提交")
    void testUpdateScoreRateAfterDeadline_noData() {
        when(practiceQuestionMapper.findpqByPracticeId(1L)).thenReturn(Collections.emptyList());
        assertDoesNotThrow(() -> practiceService.updateScoreRateAfterDeadline(1L));

        PracticeQuestion pq = new PracticeQuestion();
        pq.setQuestionId(1L);
        pq.setScore(10);
        when(practiceQuestionMapper.findpqByPracticeId(2L)).thenReturn(Arrays.asList(pq));
        when(submissionMapper.findByPracticeId(2L)).thenReturn(Collections.emptyList());
        assertDoesNotThrow(() -> practiceService.updateScoreRateAfterDeadline(2L));
    }

    @Test
    @DisplayName("getAllEndedPracticeIds - 正常获取")
    void testGetAllEndedPracticeIds_success() {
        List<Long> ids = Arrays.asList(1L, 2L);
        when(practiceMapper.findAllEndedPracticeIds(any(LocalDateTime.class))).thenReturn(ids);

        List<Long> result = practiceService.getAllEndedPracticeIds();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("getAllEndedPracticeIds - 无数据")
    void testGetAllEndedPracticeIds_empty() {
        when(practiceMapper.findAllEndedPracticeIds(any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        List<Long> result = practiceService.getAllEndedPracticeIds();

        assertTrue(result.isEmpty());
    }
}
