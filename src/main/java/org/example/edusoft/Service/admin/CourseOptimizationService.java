package org.example.edusoft.service.admin;

import org.example.edusoft.ai.AIServiceClient;
import org.example.edusoft.entity.course.Course;
import org.example.edusoft.entity.course.CourseSection;
import org.example.edusoft.mapper.course.CourseMapper;
import org.example.edusoft.mapper.course.CourseSectionMapper;
import org.example.edusoft.mapper.admin.DashboardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourseOptimizationService {

    @Autowired
    private AIServiceClient aiServiceClient;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseSectionMapper courseSectionMapper;

    @Autowired
    private DashboardMapper dashboardMapper;

    public Map<String, Object> generateOptimizationSuggestions(
            Long courseId,
            Long sectionId,
            Double averageScore,
            Double errorRate,
            Integer studentCount
    ) {
        // 获取课程和章节信息
        Course course = courseMapper.selectById(courseId);
        CourseSection section = courseSectionMapper.selectById(sectionId);

        if (course == null || section == null) {
            throw new IllegalArgumentException("Course or section not found");
        }

        // 获取详细统计数据
        Map<String, Object> statistics = dashboardMapper.getCourseStatistics(courseId, sectionId);
        List<Map<String, Object>> topWrongQuestions = dashboardMapper.getTopWrongQuestions(courseId, sectionId);

        // 构建请求数据
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("courseName", course.getName());
        requestData.put("sectionName", section.getTitle());

        requestData.put("averageScore", statistics != null ? (Double) statistics.get("average_score") : null);
        requestData.put("errorRate", statistics != null ? (Double) statistics.get("error_rate") : null);
        requestData.put("studentCount", statistics != null ? (Integer) statistics.get("student_count") : null);
        requestData.put("topWrongQuestions", topWrongQuestions);

        // 调用 AI 服务
        return aiServiceClient.optimizeCourse(requestData);
    }
}
