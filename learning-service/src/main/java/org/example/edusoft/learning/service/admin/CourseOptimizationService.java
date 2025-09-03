package org.example.edusoft.learning.service.admin;

import org.example.edusoft.learning.ai.AIServiceClient;
import org.example.edusoft.learning.client.CourseClient;
import org.example.edusoft.learning.mapper.admin.DashboardMapper;
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
    private DashboardMapper dashboardMapper;

    @Autowired
    private CourseClient courseClient;

    public Map<String, Object> generateOptimizationSuggestions(
            Long courseId,
            Long sectionId,
            Double averageScore,
            Double errorRate,
            Integer studentCount
    ) {
        // 通过CourseClient获取课程和章节信息，兼容Result包裹
        Map<String, Object> courseResp = courseClient.getCourseById(courseId);
        Map<String, Object> sectionResp = courseClient.getSectionById(sectionId);
        System.out.println("bbb======================================+++++++++++++++++++++++++++++++");
        System.out.println(courseResp);
        System.out.println(sectionResp);

        Map<String, Object> course = null;
        Map<String, Object> section = null;
        if (courseResp != null) {
            Object data = courseResp.get("data");
            course = (data instanceof Map) ? (Map<String, Object>) data : courseResp;
        }
        if (sectionResp != null) {
            Object data = sectionResp.get("data");
            section = (data instanceof Map) ? (Map<String, Object>) data : sectionResp;
        }
        System.out.println("xxx======================================+++++++++++++++++++++++++++++++");
        System.out.println("Course: " + course);
        System.out.println("Section: " + section);

        // 参考 PracticeServiceImpl，安全获取名称，避免空指针
        String courseName = "未知课程";
        String sectionName = "未知章节";
        if (course != null) {
            Object nameObj = course.get("name");
            if (nameObj == null) nameObj = course.get("title");
            if (nameObj == null) nameObj = course.get("course_name");
            if (nameObj == null) nameObj = course.get("courseName");
            if (nameObj != null) {
                courseName = nameObj != null ? nameObj.toString() : "未知课程";
            }
        }
        if (section != null) {
            Object nameObj = section.get("title");
            if (nameObj == null) nameObj = section.get("name");
            if (nameObj == null) nameObj = section.get("sectionName");
            if (nameObj == null) nameObj = section.get("section_title");
            if (nameObj == null) nameObj = section.get("sectionTitle");
            if (nameObj == null) nameObj = section.get("title_cn");
            if (nameObj != null) {
                sectionName = nameObj != null ? nameObj.toString() : "未知章节";
            }
        }

        // 获取详细统计数据
        Map<String, Object> statistics = dashboardMapper.getCourseStatistics(courseId, sectionId);
        List<Map<String, Object>> topWrongQuestions = dashboardMapper.getTopWrongQuestions(courseId, sectionId);

        // 构建请求数据
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("courseName", courseName);
        requestData.put("sectionName", sectionName);

        requestData.put("averageScore", statistics != null && statistics.get("average_score") != null ? statistics.get("average_score") : averageScore);
        requestData.put("errorRate", statistics != null && statistics.get("error_rate") != null ? statistics.get("error_rate") : errorRate);
        requestData.put("studentCount", statistics != null && statistics.get("student_count") != null ? statistics.get("student_count") : studentCount);
        requestData.put("topWrongQuestions", topWrongQuestions);

        // 调用 AI 服务，直接传递 Map（JSON），不要用 toString()
        return aiServiceClient.optimizeCourse(requestData);

    }
}

