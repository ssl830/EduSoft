package org.example.edusoft.service;

import org.example.edusoft.ai.AIServiceClient;
import org.example.edusoft.entity.course.Course;
import org.example.edusoft.entity.course.CourseSection;
import org.example.edusoft.mapper.course.CourseMapper;
import org.example.edusoft.mapper.course.CourseSectionMapper;
import org.example.edusoft.mapper.admin.DashboardMapper;
import org.example.edusoft.service.admin.CourseOptimizationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseOptimizationServiceTest {

    @Mock
    private AIServiceClient aiServiceClient;
    @Mock
    private CourseMapper courseMapper;
    @Mock
    private CourseSectionMapper courseSectionMapper;
    @Mock
    private DashboardMapper dashboardMapper;

    @InjectMocks
    private CourseOptimizationService courseOptimizationService;

    @Test
    @DisplayName("生成优化建议 - 正常流程")
    void testGenerateOptimizationSuggestions_success() {
        Long courseId = 1L;
        Long sectionId = 2L;
        Double averageScore = 80.0;
        Double errorRate = 0.1;
        Integer studentCount = 30;

        Course course = new Course();
        course.setName("数学");
        CourseSection section = new CourseSection();
        section.setTitle("函数");

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("average_score", averageScore);
        statistics.put("error_rate", errorRate);
        statistics.put("student_count", studentCount);

        List<Map<String, Object>> topWrongQuestions = Arrays.asList(new HashMap<>());

        when(courseMapper.selectById(courseId)).thenReturn(course);
        when(courseSectionMapper.selectById(sectionId)).thenReturn(section);
        when(dashboardMapper.getCourseStatistics(courseId, sectionId)).thenReturn(statistics);
        when(dashboardMapper.getTopWrongQuestions(courseId, sectionId)).thenReturn(topWrongQuestions);

        Map<String, Object> aiResult = new HashMap<>();
        aiResult.put("suggestion", "加强函数讲解");
        when(aiServiceClient.optimizeCourse(any())).thenReturn(aiResult);

        Map<String, Object> result = courseOptimizationService.generateOptimizationSuggestions(
                courseId, sectionId, averageScore, errorRate, studentCount);

        assertEquals("加强函数讲解", result.get("suggestion"));
    }

    @Test
    @DisplayName("生成优化建议 - 课程不存在")
    void testGenerateOptimizationSuggestions_courseNotFound() {
        Long courseId = 1L;
        Long sectionId = 2L;

        when(courseMapper.selectById(courseId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () ->
                courseOptimizationService.generateOptimizationSuggestions(courseId, sectionId, 80.0, 0.1, 30));
    }

    @Test
    @DisplayName("生成优化建议 - 章节不存在")
    void testGenerateOptimizationSuggestions_sectionNotFound() {
        Long courseId = 1L;
        Long sectionId = 2L;
        Course course = new Course();
        when(courseMapper.selectById(courseId)).thenReturn(course);
        when(courseSectionMapper.selectById(sectionId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () ->
                courseOptimizationService.generateOptimizationSuggestions(courseId, sectionId, 80.0, 0.1, 30));
    }

    @Test
    @DisplayName("生成优化建议 - 统计数据为空")
    void testGenerateOptimizationSuggestions_statisticsNull() {
        Long courseId = 1L;
        Long sectionId = 2L;
        Course course = new Course();
        course.setName("数学");
        CourseSection section = new CourseSection();
        section.setTitle("函数");

        when(courseMapper.selectById(courseId)).thenReturn(course);
        when(courseSectionMapper.selectById(sectionId)).thenReturn(section);
        when(dashboardMapper.getCourseStatistics(courseId, sectionId)).thenReturn(null);
        when(dashboardMapper.getTopWrongQuestions(courseId, sectionId)).thenReturn(Collections.emptyList());

        // 统计数据为null时，服务应该能够处理而不抛出异常
        Map<String, Object> result = courseOptimizationService.generateOptimizationSuggestions(
                courseId, sectionId, 80.0, 0.1, 30);

        // 根据你的实现，这里可能是空Map或者包含默认值的Map
        assertNotNull(result);
        // 如果服务在这种情况下返回空Map
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("生成优化建议 - AI服务异常")
    void testGenerateOptimizationSuggestions_aiServiceException() {
        Long courseId = 1L;
        Long sectionId = 2L;
        Course course = new Course();
        course.setName("数学");
        CourseSection section = new CourseSection();
        section.setTitle("函数");

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("average_score", 80.0);
        statistics.put("error_rate", 0.1);
        statistics.put("student_count", 30);

        when(courseMapper.selectById(courseId)).thenReturn(course);
        when(courseSectionMapper.selectById(sectionId)).thenReturn(section);
        when(dashboardMapper.getCourseStatistics(courseId, sectionId)).thenReturn(statistics);
        when(dashboardMapper.getTopWrongQuestions(courseId, sectionId)).thenReturn(Collections.emptyList());
        when(aiServiceClient.optimizeCourse(any())).thenThrow(new RuntimeException("AI服务异常"));

        assertThrows(RuntimeException.class, () ->
                courseOptimizationService.generateOptimizationSuggestions(courseId, sectionId, 80.0, 0.1, 30));
    }
}

