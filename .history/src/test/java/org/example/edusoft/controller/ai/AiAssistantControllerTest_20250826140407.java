package org.example.edusoft.controller.ai;

import org.example.edusoft.service.ai.AiAssistantService;
import org.example.edusoft.service.selfpractice.SelfPracticeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AiAssistantController.class)
class AiAssistantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AiAssistantService aiAssistantService;

    @MockBean
    private SelfPracticeService selfPracticeService;

    // ========== 示例1：uploadEmbeddingFile ==========

    @Test
    void uploadEmbeddingFile_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());
        Map<String, Object> resp = Map.of("status", "ok");

        Mockito.when(aiAssistantService.uploadEmbeddingFile(any(), anyString())).thenReturn(resp);

        mockMvc.perform(multipart("/api/ai/embedding/upload")
                .file(file)
                .param("course_id", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void uploadEmbeddingFile_fail() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());
        Mockito.when(aiAssistantService.uploadEmbeddingFile(any(), anyString()))
            .thenThrow(new RuntimeException("upload error"));

        mockMvc.perform(multipart("/api/ai/embedding/upload")
                .file(file)
                .param("course_id", "123"))
                .andExpect(status().is5xxServerError());
    }

    // ========== 示例2：generateTeachingContent ==========

    @Test
    void generateTeachingContent_success() throws Exception {
        Map<String, Object> req = Map.of("topic", "math");
        Map<String, Object> resp = Map.of("result", "teaching content");

        Mockito.when(aiAssistantService.generateTeachingContent(anyMap())).thenReturn(resp);

        mockMvc.perform(post("/api/ai/rag/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"topic\":\"math\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("teaching content"));
    }

    @Test
    void generateTeachingContent_fail() throws Exception {
        Mockito.when(aiAssistantService.generateTeachingContent(anyMap()))
            .thenThrow(new RuntimeException("service error"));

        mockMvc.perform(post("/api/ai/rag/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"topic\":\"math\"}"))
                .andExpect(status().is5xxServerError());
    }

    // ========== 其余各方法测试思路简要 ==========

    // 你可以依照上面写法，进行其余方法单元测试：
    // - mock对应service方法，返回正常结果（正向用例）
    // - mock service抛异常或返回null、错误结构（反向用例）
    // - 调用MockMvc发起相应的HTTP请求，断言返回结果/状态码/JSON内容

    /*
    // 示例（伪代码）：

    @Test
    void methodName_success() throws Exception {
        // Arrange
        Mockito.when(aiAssistantService.methodName(...)).thenReturn(...);
        // Act & Assert
        mockMvc.perform(post("/api/ai/xxx")....)
            .andExpect(status().isOk())
            .andExpect(...);
    }

    @Test
    void methodName_fail() throws Exception {
        Mockito.when(aiAssistantService.methodName(...)).thenThrow(new RuntimeException("error"));
        mockMvc.perform(post("/api/ai/xxx")....)
            .andExpect(status().is5xxServerError());
    }
    */

    // 建议：如遇特殊逻辑（如依赖StpUtil登录等），可用静态mock方式或单独测试service逻辑。

}