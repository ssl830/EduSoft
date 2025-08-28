package org.example.edusoft.controller.selfpractice;

import org.example.edusoft.common.Result;
import org.example.edusoft.entity.selfpractice.SelfAnswer;
import org.example.edusoft.entity.selfpractice.SelfSubmission;
import org.example.edusoft.entity.practice.Question;
import org.example.edusoft.entity.practice.Question.QuestionType;
import org.example.edusoft.service.ai.AiAssistantService;
import org.example.edusoft.service.practice.QuestionService;
import org.example.edusoft.service.practice.PracticeService;
import org.example.edusoft.mapper.selfpractice.SelfSubmissionMapper;
import org.example.edusoft.mapper.selfpractice.SelfAnswerMapper;
import org.example.edusoft.service.selfpractice.SelfPracticeService;

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

@WebMvcTest(SelfPracticeController.class)
class SelfPracticeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AiAssistantService aiAssistantService;
    @MockBean
    private QuestionService questionService;
    @MockBean
    private PracticeService practiceService;
    @MockBean
    private SelfSubmissionMapper submissionMapper;
    @MockBean
    private SelfAnswerMapper answerMapper;
    @MockBean
    private SelfPracticeService selfPracticeService;

    void mockLoginTrue() {
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
                .when(cn.dev33.satoken.stp.StpUtil::isLogin).thenReturn(true);
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
                .when(cn.dev33.satoken.stp.StpUtil::getLoginIdAsLong).thenReturn(101L);
    }

    void mockLoginFalse() {
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
                .when(cn.dev33.satoken.stp.StpUtil::isLogin).thenReturn(false);
    }

    // saveProgress
    @Test
    void saveProgress_success() throws Exception {
        String json = "{\"practiceId\":1,\"answers\":[{\"questionId\":1,\"answer\":\"A\"}]}";
        mockMvc.perform(post("/api/selfpractice/save-progress")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true))
                .andExpect(jsonPath("$.msg").value("进度已暂存"));
    }

    // submitPractice success (singlechoice)
    @Test
    void submitPractice_success_singlechoice() throws Exception {
        mockLoginTrue();
        // practice exists
        Mockito.when(selfPracticeService.checkPracticeExists(1L)).thenReturn(true);

        // question
        Question q = new Question();
        q.setId(1L);
        q.setType(QuestionType.singlechoice);
        q.setAnswer("A");
        Mockito.when(questionService.getQuestionDetail(1L)).thenReturn(q);

        Mockito.doNothing().when(practiceService).addWrongQuestion(anyLong(), anyLong(), anyString());
        Mockito.doNothing().when(submissionMapper).insert(any(SelfSubmission.class));
        Mockito.doNothing().when(answerMapper).insert(any(SelfAnswer.class));

        String json = "{\"practiceId\":1,\"answers\":[{\"questionId\":1,\"answer\":\"A\",\"score\":10,\"type\":\"singlechoice\"}]}";
        mockMvc.perform(post("/api/selfpractice/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("评分完成"))
                .andExpect(jsonPath("$.data.totalScore").value(10));
    }

    // submitPractice fail: not login
    @Test
    void submitPractice_fail_notLogin() throws Exception {
        mockLoginFalse();
        String json = "{\"practiceId\":1,\"answers\":[{\"questionId\":1,\"answer\":\"A\"}]}";
        mockMvc.perform(post("/api/selfpractice/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("请先登录"));
    }

    // submitPractice fail: practiceId not exist
    @Test
    void submitPractice_fail_practiceNotExist() throws Exception {
        mockLoginTrue();
        Mockito.when(selfPracticeService.checkPracticeExists(1L)).thenReturn(false);

        String json = "{\"practiceId\":1,\"answers\":[{\"questionId\":1,\"answer\":\"A\"}]}";
        mockMvc.perform(post("/api/selfpractice/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("提交失败：练习ID不存在"));
    }

    // submitPractice fail: answers empty
    @Test
    void submitPractice_fail_answersEmpty() throws Exception {
        mockLoginTrue();
        Mockito.when(selfPracticeService.checkPracticeExists(1L)).thenReturn(true);

        String json = "{\"practiceId\":1,\"answers\":[]}";
        mockMvc.perform(post("/api/selfpractice/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("答案列表不能为空"));
    }

    // submitPractice success: subjective type, ai evaluation
    @Test
    void submitPractice_success_subjective() throws Exception {
        mockLoginTrue();
        Mockito.when(selfPracticeService.checkPracticeExists(2L)).thenReturn(true);

        // subjective question
        Question q = new Question();
        q.setId(2L);
        q.setType(QuestionType.subjective);
        q.setAnswer("REF");
        q.setContent("Q2");
        Mockito.when(questionService.getQuestionDetail(2L)).thenReturn(q);

        Map<String, Object> eval = new HashMap<>();
        eval.put("score", 8.0);
        eval.put("feedback", "Good!");
        Mockito.when(aiAssistantService.evaluateSubjectiveAnswer(anyString(), anyString(), anyString(), anyDouble()))
                .thenReturn(eval);

        Mockito.doNothing().when(practiceService).addWrongQuestion(anyLong(), anyLong(), anyString());
        Mockito.doNothing().when(submissionMapper).insert(any(SelfSubmission.class));
        Mockito.doNothing().when(answerMapper).insert(any(SelfAnswer.class));

        String json = "{\"practiceId\":2,\"answers\":[{\"questionId\":2,\"answer\":\"student ans\",\"type\":\"subjective\",\"score\":10}]}";
        mockMvc.perform(post("/api/selfpractice/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalScore").value(8.0))
                .andExpect(jsonPath("$.data.details[0].feedback").value("Good!"));
    }

    // submitPractice fail: insert submission error
    @Test
    void submitPractice_fail_submissionInsert() throws Exception {
        mockLoginTrue();
        Mockito.when(selfPracticeService.checkPracticeExists(1L)).thenReturn(true);

        Question q = new Question();
        q.setId(1L);
        q.setType(QuestionType.singlechoice);
        q.setAnswer("A");
        Mockito.when(questionService.getQuestionDetail(1L)).thenReturn(q);

        Mockito.doThrow(new RuntimeException("DB error")).when(submissionMapper).insert(any(SelfSubmission.class));

        String json = "{\"practiceId\":1,\"answers\":[{\"questionId\":1,\"answer\":\"B\",\"score\":10,\"type\":\"singlechoice\"}]}";
        mockMvc.perform(post("/api/selfpractice/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("提交失败：")));
    }

    // listHistory success
    @Test
    void listHistory_success() throws Exception {
        mockLoginTrue();
        List<Object> history = Arrays.asList(new Object(), new Object());
        Mockito.when(selfPracticeService.getHistory(101L)).thenReturn(history);

        mockMvc.perform(get("/api/selfpractice/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").exists());
    }

    // listHistory fail
    @Test
    void listHistory_fail() throws Exception {
        mockLoginTrue();
        Mockito.when(selfPracticeService.getHistory(101L)).thenThrow(new RuntimeException("fail!"));
        mockMvc.perform(get("/api/selfpractice/history"))
                .andExpect(status().is5xxServerError());
    }

    // detail success
    @Test
    void detail_success() throws Exception {
        mockLoginTrue();
        Map<String, Object> detail = new HashMap<>();
        detail.put("practiceId", 1L);
        Mockito.when(selfPracticeService.getDetail(101L, 1L)).thenReturn(detail);

        mockMvc.perform(get("/api/selfpractice/history/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.practiceId").value(1));
    }

    // detail fail
    @Test
    void detail_fail() throws Exception {
        mockLoginTrue();
        Mockito.when(selfPracticeService.getDetail(101L, 1L)).thenThrow(new RuntimeException("fail!"));
        mockMvc.perform(get("/api/selfpractice/history/1"))
                .andExpect(status().is5xxServerError());
    }
}