package org.example.edusoft.controller.practice;

import org.example.edusoft.common.domain.Result;
import org.example.edusoft.dto.practice.*;
import org.example.edusoft.entity.practice.*;
import org.example.edusoft.service.practice.ManualJudgeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ManualJudgeController.class)
class ManualJudgeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManualJudgeService manualJudgeService;

    // getPendingList success
    @Test
    void getPendingList_success() throws Exception {
        List<PendingSubmissionDTO> pendingList = Arrays.asList(new PendingSubmissionDTO(), new PendingSubmissionDTO());
        Result<List<PendingSubmissionDTO>> result = Result.ok(pendingList, "success");

        Mockito.when(manualJudgeService.getPendingSubmissionList(eq(1L), eq(2L))).thenReturn(result);

        String json = "{\"practiceId\":1,\"classId\":2}";
        mockMvc.perform(post("/api/judge/pendinglist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").exists());
    }

    // getPendingList fail
    @Test
    void getPendingList_fail() throws Exception {
        Mockito.when(manualJudgeService.getPendingSubmissionList(any(), any()))
                .thenThrow(new RuntimeException("fail!"));
        String json = "{\"practiceId\":1,\"classId\":2}";
        mockMvc.perform(post("/api/judge/pendinglist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().is5xxServerError());
    }

    // getSubmissionDetail success
    @Test
    void getSubmissionDetail_success() throws Exception {
        List<SubmissionDetailDTO> detailList = Arrays.asList(new SubmissionDetailDTO(), new SubmissionDetailDTO());
        Result<List<SubmissionDetailDTO>> result = Result.ok(detailList, "detail");
        Mockito.when(manualJudgeService.getSubmissionDetail(99L)).thenReturn(result);

        String json = "{\"submissionId\":99}";
        mockMvc.perform(post("/api/judge/pending")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").exists());
    }

    // getSubmissionDetail fail
    @Test
    void getSubmissionDetail_fail() throws Exception {
        Mockito.when(manualJudgeService.getSubmissionDetail(any()))
                .thenThrow(new RuntimeException("fail!"));
        String json = "{\"submissionId\":99}";
        mockMvc.perform(post("/api/judge/pending")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().is5xxServerError());
    }

    // judgeAnswers success
    @Test
    void judgeAnswers_success() throws Exception {
        Result<Void> result = Result.ok(null, "批改成功");
        Mockito.when(manualJudgeService.judgeSubmission(any())).thenReturn(result);

        String json = "{\"submissionId\":88,\"score\":100}";
        mockMvc.perform(post("/api/judge/judge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("批改成功"));
    }

    // judgeAnswers fail
    @Test
    void judgeAnswers_fail() throws Exception {
        Mockito.when(manualJudgeService.judgeSubmission(any()))
                .thenThrow(new RuntimeException("fail!"));
        String json = "{\"submissionId\":88,\"score\":100}";
        mockMvc.perform(post("/api/judge/judge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().is5xxServerError());
    }
}