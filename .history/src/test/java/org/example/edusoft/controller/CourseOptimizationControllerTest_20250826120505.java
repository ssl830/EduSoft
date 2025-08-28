package org.example.edusoft.controller.admin;

import org.example.edusoft.common.Result;
import org.example.edusoft.service.admin.CourseOptimizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseOptimizationController.class)
class CourseOptimizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseOptimizationService courseOptimizationService;

    private String validRequestBody;

    @BeforeEach
    void setUp() {
        validRequestBody = "{"
                + "\"courseId\":1,"
                + "\"sectionId\":2,"
                + "\"averageScore\":90.5,"
                + "\"errorRate\":0.12,"
                + "\"studentCount\":30"
                + "}";
    }

    // 正向用例：服务调用成功，返回建议
    @Test
    void getOptimizationSuggestions_success() throws Exception {
        Map<String, Object> mockSuggestions = new HashMap<>();
        mockSuggestions.put("advice", "Improve section content");

        Mockito.when(courseOptimizationService.generateOptimizationSuggestions(
                anyLong(), anyLong(), anyDouble(), anyDouble(), anyInt()
        )).thenReturn(mockSuggestions);

        mockMvc.perform(post("/api/admin/dashboard/optimize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.advice").value("Improve section content"));
    }

    // 反向用例：service抛异常，返回错误信息
    @Test
    void getOptimizationSuggestions_serviceThrowsException() throws Exception {
        Mockito.when(courseOptimizationService.generateOptimizationSuggestions(
                anyLong(), anyLong(), anyDouble(), anyDouble(), anyInt()
        )).thenThrow(new RuntimeException("AI服务异常"));

        mockMvc.perform(post("/api/admin/dashboard/optimize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.containsString("Failed to generate optimization suggestions")));
    }
}