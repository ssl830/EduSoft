package org.example.edusoft.service;

import org.example.edusoft.mapper.admin.DashboardMapper;
import org.example.edusoft.service.admin.DashboardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private DashboardMapper dashboardMapper;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    @DisplayName("获取基础统计数据 - 正常返回")
    void testGetBasicStatistics_success() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now();

        when(dashboardMapper.countTeacherUploadResource(start, end)).thenReturn(5);
        when(dashboardMapper.countTeacherCreateHomework(start, end)).thenReturn(2);
        when(dashboardMapper.countTeacherCreatePractice(start, end)).thenReturn(3);
        when(dashboardMapper.countTeacherCorrectPractice(start, end)).thenReturn(4);
        when(dashboardMapper.countTeacherCreateQuestion(start, end)).thenReturn(1);

        when(dashboardMapper.countStudentDownloadResource(start, end)).thenReturn(10);
        when(dashboardMapper.countStudentSubmitHomework(start, end)).thenReturn(8);
        when(dashboardMapper.countStudentSubmitPractice(start, end)).thenReturn(7);
        when(dashboardMapper.countStudentAssistantQuestions(start, end)).thenReturn(6);

        Map<String, Object> result = dashboardService.getBasicStatistics(start, end);

        assertNotNull(result.get("teacher"));
        assertNotNull(result.get("student"));
        assertEquals(5, ((Map)result.get("teacher")).get("uploadResource"));
        assertEquals(10, ((Map)result.get("student")).get("downloadResource"));
    }

    @Test
    @DisplayName("获取基础统计数据 - Mapper返回0")
    void testGetBasicStatistics_zero() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now();

        when(dashboardMapper.countTeacherUploadResource(start, end)).thenReturn(0);
        when(dashboardMapper.countTeacherCreateHomework(start, end)).thenReturn(0);
        when(dashboardMapper.countTeacherCreatePractice(start, end)).thenReturn(0);
        when(dashboardMapper.countTeacherCorrectPractice(start, end)).thenReturn(0);
        when(dashboardMapper.countTeacherCreateQuestion(start, end)).thenReturn(0);

        when(dashboardMapper.countStudentDownloadResource(start, end)).thenReturn(0);
        when(dashboardMapper.countStudentSubmitHomework(start, end)).thenReturn(0);
        when(dashboardMapper.countStudentSubmitPractice(start, end)).thenReturn(0);
        when(dashboardMapper.countStudentAssistantQuestions(start, end)).thenReturn(0);

        Map<String, Object> result = dashboardService.getBasicStatistics(start, end);

        assertEquals(0, ((Map)result.get("teacher")).get("uploadResource"));
        assertEquals(0, ((Map)result.get("student")).get("downloadResource"));
    }

    @Test
    @DisplayName("获取教学效率指数 - 正常返回")
    void testGetTeachingEfficiencyMetrics_success() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now();

        when(dashboardMapper.getAiServiceAverageDuration(anyString(), eq(start), eq(end))).thenReturn(1.0);
        when(dashboardMapper.getAiServiceCallCount(anyString(), eq(start), eq(end))).thenReturn(2);

        when(dashboardMapper.getClassCourseSectionStats(start, end)).thenReturn(Collections.emptyList());
        when(dashboardMapper.getAllCoursesAndSections()).thenReturn(Collections.emptyList());
        when(dashboardMapper.getAllClasses()).thenReturn(Collections.emptyList());

        Map<String, Object> result = dashboardService.getTeachingEfficiencyMetrics(start, end);

        assertTrue(result.containsKey("lessonPrepAvgDuration"));
        assertTrue(result.containsKey("lessonPrepCallCount"));
        assertTrue(result.containsKey("courseOptimizationDirection"));
    }

    @Test
    @DisplayName("获取教学效率指数 - Mapper返回null和空")
    void testGetTeachingEfficiencyMetrics_emptyStats() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now();

        when(dashboardMapper.getAiServiceAverageDuration(anyString(), eq(start), eq(end))).thenReturn(0.0);
        when(dashboardMapper.getAiServiceCallCount(anyString(), eq(start), eq(end))).thenReturn(0);

        when(dashboardMapper.getClassCourseSectionStats(start, end)).thenReturn(null);
        when(dashboardMapper.getAllCoursesAndSections()).thenReturn(null);
        when(dashboardMapper.getAllClasses()).thenReturn(null);

        Map<String, Object> result = dashboardService.getTeachingEfficiencyMetrics(start, end);

        // 验证 Map 包含预期的默认值
        assertEquals("0.00", result.get("lessonPrepAvgDuration"));
        assertEquals(0, result.get("lessonPrepCallCount"));
        assertEquals("0.00", result.get("exerciseDesignAvgDuration"));
        assertEquals(0, result.get("exerciseDesignCallCount"));
        assertTrue(((Map) result.get("classNames")).isEmpty());
        assertTrue(((Map) result.get("courseSectionNames")).isEmpty());
        assertTrue(((String) result.get("courseOptimizationDirection")).contains("暂无明显薄弱环节"));
    }

    @Test
    @DisplayName("获取学生学习效果 - 正常返回")
    void testGetStudentLearningEffect_success() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now();

        List<Map<String, Object>> correctnessBySection = Arrays.asList(new HashMap<>());
        List<Map<String, Object>> topWrongKnowledgePoints = Arrays.asList(new HashMap<>());
        List<Map<String, Object>> allCoursesAndSections = Arrays.asList(new HashMap<>());

        when(dashboardMapper.getAverageCorrectnessBySection(start, end)).thenReturn(correctnessBySection);
        when(dashboardMapper.getTopWrongKnowledgePoints(start, end)).thenReturn(topWrongKnowledgePoints);
        when(dashboardMapper.getAllCoursesAndSections()).thenReturn(allCoursesAndSections);

        Map<String, Object> result = dashboardService.getStudentLearningEffect(start, end);

        assertEquals(correctnessBySection, result.get("correctnessBySection"));
        assertEquals(topWrongKnowledgePoints, result.get("topWrongKnowledgePoints"));
        assertTrue(result.containsKey("courseSectionNames"));
    }

    @Test
    @DisplayName("获取学生学习效果 - Mapper返回空列表")
    void testGetStudentLearningEffect_empty() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now();

        when(dashboardMapper.getAverageCorrectnessBySection(start, end)).thenReturn(Collections.emptyList());
        when(dashboardMapper.getTopWrongKnowledgePoints(start, end)).thenReturn(Collections.emptyList());
        when(dashboardMapper.getAllCoursesAndSections()).thenReturn(Collections.emptyList());

        Map<String, Object> result = dashboardService.getStudentLearningEffect(start, end);

        assertTrue(((List)result.get("correctnessBySection")).isEmpty());
        assertTrue(((List)result.get("topWrongKnowledgePoints")).isEmpty());
        assertTrue(((Map)result.get("courseSectionNames")).isEmpty());
    }

    @Test
    @DisplayName("获取大屏概览 - 正常返回")
    void testGetDashboardOverview_success() {
        // mock getBasicStatistics, getTeachingEfficiencyMetrics, getStudentLearningEffect
        DashboardService spyService = spy(dashboardService);
        doReturn(new HashMap<>()).when(spyService).getBasicStatistics(any(), any());
        doReturn(new HashMap<>()).when(spyService).getTeachingEfficiencyMetrics(any(), any());
        doReturn(new HashMap<>()).when(spyService).getStudentLearningEffect(any(), any());

        Map<String, Object> result = spyService.getDashboardOverview();

        assertTrue(result.containsKey("today"));
        assertTrue(result.containsKey("week"));
        assertTrue(result.containsKey("todayEfficiency"));
        assertTrue(result.containsKey("weekEfficiency"));
        assertTrue(result.containsKey("todayLearningEffect"));
        assertTrue(result.containsKey("weekLearningEffect"));
    }

    @Test
    @DisplayName("获取大屏概览 - getBasicStatistics异常")
    void testGetDashboardOverview_basicStatisticsException() {
        DashboardService spyService = spy(dashboardService);

        // 模拟 getBasicStatistics 抛出异常
        doThrow(new RuntimeException("error"))
            .when(spyService)
            .getBasicStatistics(any(), any());

        // 验证调用 getDashboardOverview 时确实抛出了异常
        assertThrows(RuntimeException.class, () -> spyService.getDashboardOverview());
    }
}

