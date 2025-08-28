package org.example.edusoft.service.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.edusoft.ai.AIServiceClient;
import org.example.edusoft.dto.ai.TeachingContentRequest;
import org.example.edusoft.dto.ai.TeachingContentResponse;
import org.example.edusoft.service.course.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TeachingContentService {
    
    @Autowired
    private AIServiceClient aiServiceClient;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 根据课程大纲生成教学内容
     */
    public TeachingContentResponse generateTeachingContent(TeachingContentRequest request) {
        // 1. 调用AI服务生成教学内容
        Map<String, Object> aiResponse = aiServiceClient.generateTeachingContent(
            request.getCourseOutline(),
            request.getCourseName(),
            request.getExpectedHours()
        );
        
        // 2. 转换响应格式
        TeachingContentResponse response = objectMapper.convertValue(aiResponse, TeachingContentResponse.class);
        
        // 3. 保存到课程资源（可选，根据需求实现）
        // TODO: 将生成的教学内容保存为课程资源
        
        return response;
    }
} 