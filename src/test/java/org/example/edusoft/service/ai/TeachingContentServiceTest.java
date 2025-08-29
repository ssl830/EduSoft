package org.example.edusoft.service.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.edusoft.ai.AIServiceClient;
import org.example.edusoft.dto.ai.TeachingContentRequest;
import org.example.edusoft.dto.ai.TeachingContentResponse;
import org.example.edusoft.service.course.CourseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.example.edusoft.service.ai.TeachingContentService;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeachingContentServiceTest {

    @Mock
    private AIServiceClient aiServiceClient;

    @Mock
    private CourseService courseService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TeachingContentService teachingContentService;

    @Test
    @DisplayName("generateTeachingContent - 正常生成教学内容")
    void testGenerateTeachingContent_success() {
        TeachingContentRequest request = new TeachingContentRequest();
        request.setCourseOutline("outline");
        request.setCourseName("Math");
        request.setExpectedHours(10);

        Map<String, Object> aiResponse = new HashMap<>();
        aiResponse.put("totalHours", "10");
        TeachingContentResponse response = new TeachingContentResponse();
        response.setTotalHours(10);

        when(aiServiceClient.generateTeachingContent("outline", "Math", 10)).thenReturn(aiResponse);
        when(objectMapper.convertValue(aiResponse, TeachingContentResponse.class)).thenReturn(response);

        TeachingContentResponse result = teachingContentService.generateTeachingContent(request);
        assertNotNull(result);
        assertEquals(10, result.getTotalHours()); // 修正为直接访问字段
    }

    @Test
    @DisplayName("generateTeachingContent - AI服务返回空")
    void testGenerateTeachingContent_aiReturnsNull() {
        TeachingContentRequest request = new TeachingContentRequest();
        request.setCourseOutline("outline");
        request.setCourseName("Math");
        request.setExpectedHours(10);

        when(aiServiceClient.generateTeachingContent("outline", "Math", 10)).thenReturn(null);
        when(objectMapper.convertValue(null, TeachingContentResponse.class)).thenReturn(null);

        TeachingContentResponse result = teachingContentService.generateTeachingContent(request);

        assertNull(result);
    }
}
