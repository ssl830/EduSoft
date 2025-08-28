package org.example.edusoft.controller.practice;

import org.example.edusoft.entity.practice.Question;
import org.example.edusoft.entity.practice.QuestionListDTO;
import org.example.edusoft.service.practice.QuestionService;
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

@WebMvcTest(QuestionController.class)
class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuestionService questionService;

    // createQuestion
    @Test
    void createQuestion_success() throws Exception {
        Question q = new Question();
        q.setId(123L);
        Mockito.when(questionService.createQuestion(any())).thenReturn(q);

        mockMvc.perform(post("/api/practice/question/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"abc\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("题目创建成功"))
                .andExpect(jsonPath("$.data.questionId").value(123));
    }

    @Test
    void createQuestion_fail() throws Exception {
        Mockito.when(questionService.createQuestion(any())).thenThrow(new RuntimeException("fail!"));

        mockMvc.perform(post("/api/practice/question/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"abc\"}"))
                .andExpect(status().is5xxServerError());
    }

    // updateQuestion
    @Test
    void updateQuestion_success() throws Exception {
        Question q = new Question();
        q.setId(5L);
        Mockito.when(questionService.updateQuestion(any())).thenReturn(q);

        mockMvc.perform(put("/api/practice/question/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"def\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("题目更新成功"))
                .andExpect(jsonPath("$.data.id").value(5));
    }

    @Test
    void updateQuestion_fail() throws Exception {
        Mockito.when(questionService.updateQuestion(any())).thenThrow(new RuntimeException("fail!"));

        mockMvc.perform(put("/api/practice/question/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"def\"}"))
                .andExpect(status().is5xxServerError());
    }

    // getQuestionList
    @Test
    void getQuestionList_success_notCourse() throws Exception {
        List<QuestionListDTO> list = Arrays.asList(new QuestionListDTO(), new QuestionListDTO());
        Mockito.when(questionService.getAllQuestions()).thenReturn(list);

        mockMvc.perform(get("/api/practice/question/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取题目列表成功"))
                .andExpect(jsonPath("$.data.questions[0]").exists());
    }

    @Test
    void getQuestionList_success_withCourse() throws Exception {
        List<QuestionListDTO> list = Arrays.asList(new QuestionListDTO(), new QuestionListDTO());
        Mockito.when(questionService.getQuestionListByCourse(2L)).thenReturn(list);

        mockMvc.perform(get("/api/practice/question/list?courseId=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.questions[0]").exists());
    }

    @Test
    void getQuestionList_fail() throws Exception {
        Mockito.when(questionService.getAllQuestions()).thenThrow(new RuntimeException("fail!"));

        mockMvc.perform(get("/api/practice/question/list"))
                .andExpect(status().is5xxServerError());
    }

    // getQuestionDetail
    @Test
    void getQuestionDetail_success() throws Exception {
        Question q = new Question();
        q.setId(8L);
        Mockito.when(questionService.getQuestionDetail(8L)).thenReturn(q);

        mockMvc.perform(get("/api/practice/question/8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取题目详情成功"))
                .andExpect(jsonPath("$.data.id").value(8));
    }

    @Test
    void getQuestionDetail_fail() throws Exception {
        Mockito.when(questionService.getQuestionDetail(8L)).thenThrow(new RuntimeException("fail!"));
        mockMvc.perform(get("/api/practice/question/8"))
                .andExpect(status().is5xxServerError());
    }

    // deleteQuestion
    @Test
    void deleteQuestion_success() throws Exception {
        Mockito.doNothing().when(questionService).deleteQuestion(10L);

        mockMvc.perform(delete("/api/practice/question/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("题目删除成功"))
                .andExpect(jsonPath("$.data.questionId").value(10));
    }

    @Test
    void deleteQuestion_fail() throws Exception {
        Mockito.doThrow(new RuntimeException("fail!")).when(questionService).deleteQuestion(10L);

        mockMvc.perform(delete("/api/practice/question/10"))
                .andExpect(status().is5xxServerError());
    }

    // importQuestionsToPractice
    @Test
    void importQuestionsToPractice_success() throws Exception {
        Mockito.doNothing().when(questionService).importQuestionsToPractice(anyLong(), anyList(), anyList());

        String json = "{\"practiceId\":1,\"questionIds\":[1,2,3],\"scores\":[5,6,7]}";
        mockMvc.perform(post("/api/practice/question/import")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("题目导入成功"))
                .andExpect(jsonPath("$.data.practiceId").value(1));
    }

    @Test
    void importQuestionsToPractice_fail() throws Exception {
        Mockito.doThrow(new RuntimeException("fail!")).when(questionService).importQuestionsToPractice(anyLong(), anyList(), anyList());

        String json = "{\"practiceId\":1,\"questionIds\":[1,2,3],\"scores\":[5,6,7]}";
        mockMvc.perform(post("/api/practice/question/import")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().is5xxServerError());
    }

    // getQuestionsBySection
    @Test
    void getQuestionsBySection_success() throws Exception {
        List<Question> list = Arrays.asList(new Question(), new Question());
        Mockito.when(questionService.getQuestionsBySection(2L, 3L)).thenReturn(list);

        mockMvc.perform(get("/api/practice/question/section?courseId=2§ionId=3".replace('§', 's')))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取章节题目列表成功"))
                .andExpect(jsonPath("$.data[0]").exists());
    }

    @Test
    void getQuestionsBySection_fail() throws Exception {
        Mockito.when(questionService.getQuestionsBySection(2L, 3L)).thenThrow(new RuntimeException("fail!"));

        mockMvc.perform(get("/api/practice/question/section?courseId=2§ionId=3".replace('§', 's')))
                .andExpect(status().is5xxServerError());
    }

    // batchCreateQuestions
    @Test
    void batchCreateQuestions_success() throws Exception {
        List<Question> list = Arrays.asList(new Question(), new Question());
        Mockito.when(questionService.batchCreateQuestions(anyList())).thenReturn(list);

        String json = "[{\"title\":\"a\"},{\"title\":\"b\"}]";
        mockMvc.perform(post("/api/practice/question/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("批量创建题目成功"))
                .andExpect(jsonPath("$.data[0]").exists());
    }

    @Test
    void batchCreateQuestions_fail() throws Exception {
        Mockito.when(questionService.batchCreateQuestions(anyList())).thenThrow(new RuntimeException("fail!"));

        String json = "[{\"title\":\"a\"},{\"title\":\"b\"}]";
        mockMvc.perform(post("/api/practice/question/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().is5xxServerError());
    }
}