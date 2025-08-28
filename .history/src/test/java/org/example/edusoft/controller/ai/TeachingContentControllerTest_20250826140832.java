package org.example.edusoft.controller.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.edusoft.dto.ai.TeachingContentRequest;
import org.example.edusoft.dto.ai.TeachingContentResponse;
import org.example.edusoft.service.ai.TeachingContentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TeachingContentController.class)
public class TeachingContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeachingContentService teachingContentService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    // ---
    
    ## 正常情况测试 (Positive Test Case)

    ### 目标
    验证当`TeachingContentService`成功生成内容时，控制器能正确处理请求并返回预期的教学内容和HTTP状态码 `200 OK`。

    ### 测试步骤
    1. 准备一个`TeachingContentRequest`模拟请求数据。
    2. 准备一个`TeachingContentResponse`模拟服务层返回的数据。
    3. 使用 Mockito 模拟`teachingContentService.generateTeachingContent`方法的行为，使其返回预设的成功响应。
    4. 执行 POST 请求到 `/api/ai/teaching/generate`。
    5. 验证响应的 HTTP 状态码是否为 `200 OK`。
    6. 验证响应的 JSON 内容是否与预设的成功响应完全匹配。

    @Test
    @DisplayName("should return generated teaching content when service is successful")
    void generateTeachingContent_Success() throws Exception {
        // 1. 准备请求和响应数据
        TeachingContentRequest request = new TeachingContentRequest();
        request.setCourseOutline("课程大纲：Java Spring Boot");

        TeachingContentResponse response = new TeachingContentResponse();
        response.setContent("教学内容：介绍Spring Boot核心概念和开发流程。");
        response.setSuccess(true);
        response.setMessage("教学内容已成功生成。");

        // 2. 模拟服务层的行为
        when(teachingContentService.generateTeachingContent(any(TeachingContentRequest.class)))
                .thenReturn(response);

        // 3. 执行请求并验证结果
        mockMvc.perform(post("/api/ai/teaching/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content").value("教学内容：介绍Spring Boot核心概念和开发流程。"))
                .andExpect(jsonPath("$.message").value("教学内容已成功生成。"));
    }
    
    ---
    
    ## 异常情况测试 (Negative Test Case)

    ### 目标
    验证当`TeachingContentService`在处理请求时发生异常，控制器能正确捕获并返回 `500 Internal Server Error` 状态码。

    ### 测试步骤
    1. 准备一个`TeachingContentRequest`模拟请求数据。
    2. 使用 Mockito 模拟`teachingContentService.generateTeachingContent`方法的行为，使其抛出一个运行时异常。
    3. 执行 POST 请求到 `/api/ai/teaching/generate`。
    4. 验证响应的 HTTP 状态码是否为 `500 Internal Server Error`。

    @Test
    @DisplayName("should return 500 when an exception occurs in the service layer")
    void generateTeachingContent_Failure() throws Exception {
        // 1. 准备请求数据
        TeachingContentRequest request = new TeachingContentRequest();
        request.setCourseOutline("课程大纲：Java Spring Boot");

        // 2. 模拟服务层抛出异常
        when(teachingContentService.generateTeachingContent(any(TeachingContentRequest.class)))
                .thenThrow(new RuntimeException("服务处理失败"));

        // 3. 执行请求并验证结果
        mockMvc.perform(post("/api/ai/teaching/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
}