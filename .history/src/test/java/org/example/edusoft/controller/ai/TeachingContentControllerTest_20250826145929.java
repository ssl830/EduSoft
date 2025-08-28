package org.example.edusoft.controller.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.edusoft.dto.ai.TeachingContentRequest;
import org.example.edusoft.dto.ai.TeachingContentResponse;
import org.example.edusoft.dto.ai.TeachingContentResponse.LessonContent;
import org.example.edusoft.service.ai.TeachingContentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeachingContentController.class)
class TeachingContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeachingContentService teachingContentService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("生成教学内容-正向用例")
    void generateTeachingContent_success() throws Exception {
        // 构造请求
        TeachingContentRequest request = new TeachingContentRequest();
        request.setCourseOutline("课程大纲：Java Spring Boot");

        // 构造响应
        LessonContent lesson = new LessonContent();
        lesson.setTitle("Spring Boot入门");
        lesson.setContent("介绍Spring Boot核心概念和开发流程。");
        lesson.setPracticeContent("动手搭建一个Spring Boot项目。");
        lesson.setTeachingGuidance("建议理论+实践结合");
        lesson.setSuggestedHours(2);
        lesson.setKnowledgeSources(Arrays.asList("Java基础", "Spring框架"));

        TeachingContentResponse response = new TeachingContentResponse();
        response.setLessons(Collections.singletonList(lesson));
        response.setTotalHours(2);
        response.setTimeDistribution("理论1小时，实践1小时");
        response.setTeachingAdvice("理论与实践结合，注重动手能力培养");

        when(teachingContentService.generateTeachingContent(any(TeachingContentRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/ai/teaching/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lessons[0].title").value("Spring Boot入门"))
                .andExpect(jsonPath("$.lessons[0].content").value("介绍Spring Boot核心概念和开发流程。"))
                .andExpect(jsonPath("$.totalHours").value(2))
                .andExpect(jsonPath("$.timeDistribution").value("理论1小时，实践1小时"))
                .andExpect(jsonPath("$.teachingAdvice").value("理论与实践结合，注重动手能力培养"));
    }

    @Test
    @DisplayName("生成教学内容-反向用例（服务层异常）")
    void generateTeachingContent_serviceException() throws Exception {
        TeachingContentRequest request = new TeachingContentRequest();
        request.setCourseOutline("课程大纲：Java Spring Boot");

        when(teachingContentService.generateTeachingContent(any(TeachingContentRequest.class)))
                .thenThrow(new RuntimeException("服务处理失败"));

        mockMvc.perform(post("/api/ai/teaching/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());
    }
}