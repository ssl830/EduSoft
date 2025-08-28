package org.example.edusoft.controller.homework;

import org.example.edusoft.common.domain.Result;
import org.example.edusoft.dto.homework.HomeworkDTO;
import org.example.edusoft.dto.homework.HomeworkSubmissionDTO;
import org.example.edusoft.service.homework.HomeworkService;
import org.example.edusoft.entity.homework.Homework;
import org.example.edusoft.entity.homework.HomeworkSubmission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeworkController.class)
class HomeworkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HomeworkService homeworkService;

    // 1. createHomework
    @Test
    void createHomework_success() throws Exception {
        Mockito.when(homeworkService.createHomework(anyLong(), anyString(), any(), any(), any())).thenReturn(10L);

        MockMultipartFile file = new MockMultipartFile("file", "1.txt", "text/plain", "test".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/homework/create")
                .file(file)
                .param("class_id", "1")
                .param("title", "TestTitle")
                .param("description", "desc")
                .param("end_time", "2025-08-31T20:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("作业创建成功"))
                .andExpect(jsonPath("$.data").value(10));
    }

    @Test
    void createHomework_fail() throws Exception {
        Mockito.when(homeworkService.createHomework(anyLong(), anyString(), any(), any(), any()))
                .thenThrow(new RuntimeException("fail!"));

        MockMultipartFile file = new MockMultipartFile("file", "1.txt", "text/plain", "test".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/homework/create")
                .file(file)
                .param("class_id", "1")
                .param("title", "TestTitle")
                .param("description", "desc")
                .param("end_time", "2025-08-31T20:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("作业创建失败：")));
    }

    // 2. getHomework
    @Test
    void getHomework_success() throws Exception {
        HomeworkDTO dto = Mockito.mock(HomeworkDTO.class);
        Mockito.when(homeworkService.getHomework(2L)).thenReturn(dto);

        mockMvc.perform(get("/api/homework/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("获取作业详情成功"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void getHomework_notFound() throws Exception {
        Mockito.when(homeworkService.getHomework(2L)).thenReturn(null);

        mockMvc.perform(get("/api/homework/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("作业不存在"));
    }

    @Test
    void getHomework_fail() throws Exception {
        Mockito.when(homeworkService.getHomework(2L)).thenThrow(new RuntimeException("fail!"));

        mockMvc.perform(get("/api/homework/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("获取作业详情失败：")));
    }

    // 3. getHomeworkList
    @Test
    void getHomeworkList_success() throws Exception {
        List<HomeworkDTO> list = Arrays.asList(Mockito.mock(HomeworkDTO.class));
        Mockito.when(homeworkService.getHomeworkList(1L)).thenReturn(list);

        mockMvc.perform(get("/api/homework/list").param("classId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("获取作业列表成功"))
                .andExpect(jsonPath("$.data[0]").exists());
    }

    @Test
    void getHomeworkList_fail() throws Exception {
        Mockito.when(homeworkService.getHomeworkList(1L)).thenThrow(new RuntimeException("fail!"));

        mockMvc.perform(get("/api/homework/list").param("classId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("获取作业列表失败：")));
    }

    // 4. submitHomework
    @Test
    void submitHomework_success() throws Exception {
        Mockito.when(homeworkService.submitHomework(anyLong(), anyLong(), any())).thenReturn(33L);

        MockMultipartFile file = new MockMultipartFile("file", "sub.txt", "text/plain", "abc".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/homework/submit/9")
                .file(file)
                .param("student_id", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("作业提交成功"))
                .andExpect(jsonPath("$.data").value(33));
    }

    @Test
    void submitHomework_fail() throws Exception {
        Mockito.when(homeworkService.submitHomework(anyLong(), anyLong(), any()))
                .thenThrow(new RuntimeException("fail!"));

        MockMultipartFile file = new MockMultipartFile("file", "sub.txt", "text/plain", "abc".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/homework/submit/9")
                .file(file)
                .param("student_id", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("作业提交失败：")));
    }

    // 5. getSubmissionList
    @Test
    void getSubmissionList_success() throws Exception {
        List<HomeworkSubmissionDTO> list = Arrays.asList(Mockito.mock(HomeworkSubmissionDTO.class));
        Mockito.when(homeworkService.getSubmissionList(5L)).thenReturn(list);

        mockMvc.perform(get("/api/homework/submissions/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("获取提交列表成功"))
                .andExpect(jsonPath("$.data[0]").exists());
    }

    @Test
    void getSubmissionList_fail() throws Exception {
        Mockito.when(homeworkService.getSubmissionList(5L)).thenThrow(new RuntimeException("fail!"));

        mockMvc.perform(get("/api/homework/submissions/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("获取提交列表失败：")));
    }

    // 6. getStudentSubmission
    @Test
    void getStudentSubmission_success() throws Exception {
        HomeworkSubmissionDTO dto = Mockito.mock(HomeworkSubmissionDTO.class);
        Mockito.when(homeworkService.getStudentSubmission(8L, 7L)).thenReturn(dto);

        mockMvc.perform(get("/api/homework/submission")
                .param("homeworkId", "8")
                .param("studentId", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("获取提交记录成功"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void getStudentSubmission_notFound() throws Exception {
        Mockito.when(homeworkService.getStudentSubmission(8L, 7L)).thenReturn(null);

        mockMvc.perform(get("/api/homework/submission")
                .param("homeworkId", "8")
                .param("studentId", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("未找到提交记录"));
    }

    @Test
    void getStudentSubmission_fail() throws Exception {
        Mockito.when(homeworkService.getStudentSubmission(8L, 7L)).thenThrow(new RuntimeException("fail!"));

        mockMvc.perform(get("/api/homework/submission")
                .param("homeworkId", "8")
                .param("studentId", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("获取提交记录失败：")));
    }

    // 7. downloadHomeworkFile
    @Test
    void downloadHomeworkFile_success() throws Exception {
        // 不抛异常即为成功
        Mockito.doNothing().when(homeworkService).downloadHomeworkFile(anyLong(), any());

        mockMvc.perform(get("/api/homework/file/1"))
                .andExpect(status().isOk());
    }

    @Test
    void downloadHomeworkFile_fail() throws Exception {
        Mockito.doThrow(new RuntimeException("fail!")).when(homeworkService).downloadHomeworkFile(anyLong(), any());

        mockMvc.perform(get("/api/homework/file/1"))
                .andExpect(status().isInternalServerError());
    }

    // 8. downloadSubmissionFile
    @Test
    void downloadSubmissionFile_success() throws Exception {
        Mockito.doNothing().when(homeworkService).downloadSubmissionFile(anyLong(), any());

        mockMvc.perform(get("/api/homework/submission/file/11"))
                .andExpect(status().isOk());
    }

    @Test
    void downloadSubmissionFile_fail() throws Exception {
        Mockito.doThrow(new RuntimeException("fail!")).when(homeworkService).downloadSubmissionFile(anyLong(), any());

        mockMvc.perform(get("/api/homework/submission/file/11"))
                .andExpect(status().isInternalServerError());
    }

    // 9. deleteHomework
    @Test
    void deleteHomework_success() throws Exception {
        Mockito.doNothing().when(homeworkService).deleteHomework(1L);

        mockMvc.perform(delete("/api/homework/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("作业删除成功"));
    }

    @Test
    void deleteHomework_fail() throws Exception {
        Mockito.doThrow(new RuntimeException("fail!")).when(homeworkService).deleteHomework(1L);

        mockMvc.perform(delete("/api/homework/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("作业删除失败：")));
    }
}