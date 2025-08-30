package org.example.edusoft.course.service;

import org.example.edusoft.common.ai.CommonAIServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.HashMap;

/**
 * 课程服务中使用AI功能的示例
 */
@Service
public class CourseAIService {

    @Autowired
    private CommonAIServiceClient aiClient;

    /**
     * 为课程生成练习题
     */
    public Map<String, Object> generateCourseExercises(Long courseId, String courseContent) {
        try {
            // 调用AI生成练习题
            Map<String, Object> result = aiClient.generateExercise(courseContent);
            
            if ("success".equals(result.get("status"))) {
                // 这里可以添加课程相关的业务逻辑
                // 比如保存生成的练习题到课程数据库
                
                return Map.of(
                    "success", true,
                    "message", "练习题生成成功",
                    "data", result.get("data")
                );
            } else {
                return Map.of(
                    "success", false,
                    "message", "练习题生成失败: " + result.get("message")
                );
            }
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "message", "服务调用异常: " + e.getMessage()
            );
        }
    }

    /**
     * 课程内容智能分析
     */
    public Map<String, Object> analyzeCourseContent(String courseContent) {
        Map<String, Object> context = new HashMap<>();
        context.put("type", "course_analysis");
        context.put("content", courseContent);
        
        String question = "请分析这个课程内容的重点和难点，并给出教学建议";
        
        return aiClient.askAI(question, context);
    }

    /**
     * 生成课程教学计划
     */
    public Map<String, Object> generateTeachingPlan(String courseName, String courseOutline, Integer hours) {
        Map<String, Object> request = new HashMap<>();
        request.put("courseName", courseName);
        request.put("courseOutline", courseOutline);
        request.put("expectedHours", hours);
        
        return aiClient.callAIService("/rag/generate", request);
    }
}
