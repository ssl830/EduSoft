package org.example.edusoft.controller.homework;

import org.example.edusoft.common.domain.Result;
import org.example.edusoft.dto.homework.HomeworkDTO;
import org.example.edusoft.dto.homework.HomeworkSubmissionDTO;
import org.example.edusoft.entity.homework.Homework;
import org.example.edusoft.entity.homework.HomeworkSubmission;
import org.example.edusoft.service.homework.HomeworkService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeworkController.class)
class HomeworkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HomeworkService homeworkService;

    // createHomework success
    @Test
    void createHomework_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "homework.txt", "text/plain", "content".getBytes());
        Mockito.when(homeworkService.createHomework(anyLong(), anyString(), any(), any(), any()))
                .thenReturn(100L);

        mockMvc.perform(multipart("/api/homework/create")
                .file(file)
                .param("class_id", "1")
                .param("title", "Title")
                .param("description", "desc")
                .param("end_time", "2025-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(100))
                .andExpect(jsonPath("$.msg").value("作业创建成功"));
    }

    // createHomework fail
    @Test
    void createHomework_fail() throws Exception {
        Mockito.when(homeworkService.createHomework(anyLong(), anyString(), any(), any(), any()))
                .thenThrow(new RuntimeException("error reason"));
        mockMvc.perform(multipart("/api/homework/create")
                .param("class_id", "1")
                .param("title", "Title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("作业创建失败：")));
    }

    // getHomework success
    @Test
    void getHomework_success() throws Exception {
        HomeworkDTO dto = new HomeworkDTO();
        dto.setId(1L);
        Mockito.when(homeworkService.getHomework(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/homework/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.msg").value("获取作业详情成功"));
    }

    // getHomework not found
    @Test
    void getHomework_notFound() throws Exception {
        Mockito.when(homeworkService.getHomework(1L)).thenReturn(null);
        mockMvc.perform(get("/api/homework/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("作业不存在"));
    }

    // getHomework fail
    @Test
    void getHomework_fail() throws Exception {
        Mockito.when(homeworkService.getHomework(1L)).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(get("/api/homework/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("获取作业详情失败：")));
    }

    // getHomeworkList success
    @Test
    void getHomeworkList_success() throws Exception {
        List<HomeworkDTO> list = Arrays.asList(new HomeworkDTO(), new HomeworkDTO());
        Mockito.when(homeworkService.getHomeworkList(1L)).thenReturn(list);

        mockMvc.perform(get("/api/homework/list?classId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").exists())
                .andExpect(jsonPath("$.msg").value("获取作业列表成功"));
    }

    // getHomeworkList fail
    @Test
    void getHomeworkList_fail() throws Exception {
        Mockito.when(homeworkService.getHomeworkList(1L)).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(get("/api/homework/list?classId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("获取作业列表失败：")));
    }

    // submitHomework success
    @Test
    void submitHomework_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "submit.txt", "text/plain", "content".getBytes());
        Mockito.when(homeworkService.submitHomework(anyLong(), anyLong(), any())).thenReturn(33L);

        mockMvc.perform(multipart("/api/homework/submit/2")
                .file(file)
                .param("student_id", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(33))
                .andExpect(jsonPath("$.msg").value("作业提交成功"));
    }

    // submitHomework fail
    @Test
    void submitHomework_fail() throws Exception {
        Mockito.when(homeworkService.submitHomework(anyLong(), anyLong(), any()))
                .thenThrow(new RuntimeException("fail"));
        MockMultipartFile file = new MockMultipartFile("file", "submit.txt", "text/plain", "content".getBytes());
        mockMvc.perform(multipart("/api/homework/submit/2")
                .file(file)
                .param("student_id", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("作业提交失败：")));
    }

    // getSubmissionList success
    @Test
    void getSubmissionList_success() throws Exception {
        List<HomeworkSubmissionDTO> list = Arrays.asList(new HomeworkSubmissionDTO(), new HomeworkSubmissionDTO());
        Mockito.when(homeworkService.getSubmissionList(2L)).thenReturn(list);

        mockMvc.perform(get("/api/homework/submissions/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").exists())
                .andExpect(jsonPath("$.msg").value("获取提交列表成功"));
    }

    // getSubmissionList fail
    @Test
    void getSubmissionList_fail() throws Exception {
        Mockito.when(homeworkService.getSubmissionList(2L)).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(get("/api/homework/submissions/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("获取提交列表失败：")));
    }

    // getStudentSubmission success
    @Test
    void getStudentSubmission_success() throws Exception {
        HomeworkSubmissionDTO dto = new HomeworkSubmissionDTO();
        dto.setId(3L);
        Mockito.when(homeworkService.getStudentSubmission(2L, 10L)).thenReturn(dto);

        mockMvc.perform(get("/api/homework/submission?homeworkId=2&studentId=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(3))
                .andExpect(jsonPath("$.msg").value("获取提交记录成功"));
    }

    // getStudentSubmission not found
    @Test
    void getStudentSubmission_notFound() throws Exception {
        Mockito.when(homeworkService.getStudentSubmission(2L, 10L)).thenReturn(null);
        mockMvc.perform(get("/api/homework/submission?homeworkId=2&studentId=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("未找到提交记录"));
    }

    // getStudentSubmission fail
    @Test
    void getStudentSubmission_fail() throws Exception {
        Mockito.when(homeworkService.getStudentSubmission(2L, 10L)).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(get("/api/homework/submission?homeworkId=2&studentId=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("获取提交记录失败：")));
    }

    // downloadHomeworkFile (void)
    @Test
    void downloadHomeworkFile_success() throws Exception {
        Mockito.doNothing().when(homeworkService).downloadHomeworkFile(eq(2L), any());
        mockMvc.perform(get("/api/homework/file/2"))
                .andExpect(status().isOk());
    }

    @Test
    void downloadHomeworkFile_fail() throws Exception {
        Mockito.doThrow(new RuntimeException("fail")).when(homeworkService).downloadHomeworkFile(eq(2L), any());
        mockMvc.perform(get("/api/homework/file/2"))
                .andExpect(status().isInternalServerError());
    }

    // downloadSubmissionFile (void)
    @Test
    void downloadSubmissionFile_success() throws Exception {
        Mockito.doNothing().when(homeworkService).downloadSubmissionFile(eq(5L), any());
        mockMvc.perform(get("/api/homework/submission/file/5"))
                .andExpect(status().isOk());
    }

    @Test
    void downloadSubmissionFile_fail() throws Exception {
        Mockito.doThrow(new RuntimeException("fail")).when(homeworkService).downloadSubmissionFile(eq(5L), any());
        mockMvc.perform(get("/api/homework/submission/file/5"))
                .andExpect(status().isInternalServerError());
    }

    // deleteHomework success
    @Test
    void deleteHomework_success() throws Exception {
        Mockito.doNothing().when(homeworkService).deleteHomework(2L);
        mockMvc.perform(delete("/api/homework/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("作业删除成功"));
    }

    // deleteHomework fail
    @Test
    void deleteHomework_fail() throws Exception {
        Mockito.doThrow(new RuntimeException("fail")).when(homeworkService).deleteHomework(2L);
        mockMvc.perform(delete("/api/homework/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("作业删除失败：")));
    }
}