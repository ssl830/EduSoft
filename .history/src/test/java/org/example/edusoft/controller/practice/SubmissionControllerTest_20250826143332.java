package org.example.edusoft.controller.practice;

import org.example.edusoft.common.domain.Result;
import org.example.edusoft.dto.practice.SubmissionRequest;
import org.example.edusoft.service.practice.SubmissionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubmissionController.class)
class SubmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubmissionService submissionService;

    // 正向测试：submitPractice 正常提交
    @Test
    void submitPractice_success() throws Exception {
        Result<Long> result = Result.ok(123L, "提交成功");
        Mockito.when(submissionService.submitAndAutoJudge(eq(1L), eq(2L), anyList()))
                .thenReturn(result);

        String json = "{\"practiceId\":1,\"studentId\":2,\"answers\":[{\"questionId\":5,\"answer\":\"A\"}]}";
        mockMvc.perform(post("/api/submission/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(123))
                .andExpect(jsonPath("$.msg").value("提交成功"));
    }

    // 反向测试：submitPractice service抛出异常
    @Test
    void submitPractice_fail_serviceError() throws Exception {
        Mockito.when(submissionService.submitAndAutoJudge(anyLong(), anyLong(), anyList()))
                .thenThrow(new RuntimeException("数据库错误"));

        String json = "{\"practiceId\":1,\"studentId\":2,\"answers\":[{\"questionId\":5,\"answer\":\"A\"}]}";
        mockMvc.perform(post("/api/submission/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().is5xxServerError());
    }

    // 反向测试：提交内容不全/参数缺失
    @Test
    void submitPractice_fail_invalidRequest() throws Exception {
        // 缺少 practiceId
        String json = "{\"studentId\":2,\"answers\":[{\"questionId\":5,\"answer\":\"A\"}]}";
        // 因为是直接传给service, controller本身不校验参数, service会NPE或抛异常
        Mockito.when(submissionService.submitAndAutoJudge(any(), any(), any()))
                .thenThrow(new IllegalArgumentException("参数不全"));
        mockMvc.perform(post("/api/submission/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().is5xxServerError());
    }
}