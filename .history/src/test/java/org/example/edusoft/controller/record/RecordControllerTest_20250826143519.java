package org.example.edusoft.controller.record;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.edusoft.service.record.RecordService;
import org.example.edusoft.entity.record.StudyRecord;
import org.example.edusoft.entity.record.PracticeRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class RecordControllerTest {

    @Mock
    private RecordService recordService;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletOutputStream outputStream;

    @Mock
    private PrintWriter printWriter;

    @InjectMocks
    private RecordController recordController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(recordController).build();
        objectMapper = new ObjectMapper();
        
        // 设置response的mock行为
        when(response.getOutputStream()).thenReturn(outputStream);
        when(response.getWriter()).thenReturn(printWriter);
    }

    // ===================== getStudyRecords 测试 =====================

    @Test
    void getStudyRecords_Success() throws Exception {
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            // 准备测试数据
            Long studentId = 1L;
            List<StudyRecord> mockRecords = Arrays.asList(
                    createMockStudyRecord(1L, "Java基础"),
                    createMockStudyRecord(2L, "Spring框架")
            );

            stpUtilMock.when(StpUtil::isLogin).thenReturn(true);
            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(studentId);
            when(recordService.getStudyRecords(studentId)).thenReturn(mockRecords);

            // 执行测试
            mockMvc.perform(get("/api/record/study"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(2));

            verify(recordService, times(1)).getStudyRecords(studentId);
        }
    }

    @Test
    void getStudyRecords_NotLoggedIn() throws Exception {
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            stpUtilMock.when(StpUtil::isLogin).thenReturn(false);

            // 执行测试
            mockMvc.perform(get("/api/record/study"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpected(jsonPath("$.message").value("请先登录"));

            verify(recordService, never()).getStudyRecords(anyLong());
        }
    }

    // ===================== getStudyRecordsByCourse 测试 =====================

    @Test
    void getStudyRecordsByCourse_Success() throws Exception {
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            // 准备测试数据
            Long studentId = 1L;
            Long courseId = 100L;
            List<StudyRecord> mockRecords = Arrays.asList(
                    createMockStudyRecord(1L, "课程章节1"),
                    createMockStudyRecord(2L, "课程章节2")
            );

            stpUtilMock.when(StpUtil::isLogin).thenReturn(true);
            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(studentId);
            when(recordService.getStudyRecordsByCourse(studentId, courseId)).thenReturn(mockRecords);

            // 执行测试
            mockMvc.perform(get("/api/record/study/course/{courseId}", courseId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(2));

            verify(recordService, times(1)).getStudyRecordsByCourse(studentId, courseId);
        }
    }

    @Test
    void getStudyRecordsByCourse_NotLoggedIn() throws Exception {
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            Long courseId = 100L;
            stpUtilMock.when(StpUtil::isLogin).thenReturn(false);

            // 执行测试
            mockMvc.perform(get("/api/record/study/course/{courseId}", courseId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpected(jsonPath("$.message").value("请先登录"));

            verify(recordService, never()).getStudyRecordsByCourse(anyLong(), anyLong());
        }
    }

    // ===================== getPracticeRecords 测试 =====================

    @Test
    void getPracticeRecords_Success() throws Exception {
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            // 准备测试数据
            Long studentId = 1L;
            List<PracticeRecord> mockRecords = Arrays.asList(
                    createMockPracticeRecord(1L, "练习1"),
                    createMockPracticeRecord(2L, "练习2")
            );

            stpUtilMock.when(StpUtil::isLogin).thenReturn(true);
            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(studentId);
            when(recordService.getPracticeRecords(studentId)).thenReturn(mockRecords);

            // 执行测试
            mockMvc.perform(get("/api/record/practice"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(2));

            verify(recordService, times(1)).getPracticeRecords(studentId);
        }
    }

    @Test
    void getPracticeRecords_NotLoggedIn() throws Exception {
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            stpUtilMock.when(StpUtil::isLogin).thenReturn(false);

            // 执行测试
            mockMvc.perform(get("/api/record/practice"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("请先登录"));

            verify(recordService, never()).getPracticeRecords(anyLong());
        }
    }

    // ===================== getPracticeRecordsByCourse 测试 =====================

    @Test
    void getPracticeRecordsByCourse_Success() throws Exception {
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            // 准备测试数据
            Long studentId = 1L;
            Long courseId = 100L;
            List<PracticeRecord> mockRecords = Arrays.asList(
                    createMockPracticeRecord(1L, "课程练习1"),
                    createMockPracticeRecord(2L, "课程练习2")
            );

            stpUtilMock.when(StpUtil::isLogin).thenReturn(true);
            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(studentId);
            when(recordService.getPracticeRecordsByCourse(studentId, courseId)).thenReturn(mockRecords);

            // 执行测试
            mockMvc.perform(get("/api/record/practice/course/{courseId}", courseId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(2));

            verify(recordService, times(1)).getPracticeRecordsByCourse(studentId, courseId);
        }
    }

    @Test
    void getPracticeRecordsByCourse_NotLoggedIn() throws Exception {
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            Long courseId = 100L;
            stpUtilMock.when(StpUtil::isLogin).thenReturn(false);

            // 执行测试
            mockMvc.perform(get("/api/record/practice/course/{courseId}", courseId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpected(jsonPath("$.message").value("请先登录"));

            verify(recordService, never()).getPracticeRecordsByCourse(anyLong(), anyLong());
        }
    }

    // ===================== exportRecords 测试 =====================

    @Test
    void exportRecords_Success() throws IOException {
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            // 准备测试数据
            Long studentId = 1L;
            byte[] mockExcelData = "mock excel data".getBytes();

            stpUtilMock.when(StpUtil::isLogin).thenReturn(true);
            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(studentId);
            when(recordService.exportRecordsToExcel(studentId)).thenReturn(mockExcelData);

            // 执行测试
            recordController.exportRecords(response);

            // 验证
            verify(response).setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            verify(response).setHeader("Content-Disposition", "attachment; filename=learning_records.xlsx");
            verify(outputStream).write(mockExcelData);
            verify(recordService, times(1)).exportRecordsToExcel(studentId);
        }
    }

    @Test
    void exportRecords_NotLoggedIn() {
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            stpUtilMock.when(StpUtil::isLogin).thenReturn(false);

            // 执行测试，期待抛出异常
            try {
                recordController.exportRecords(response);
                fail("Expected RuntimeException to be thrown");
            } catch (RuntimeException e) {
                assertEquals("请先登录", e.getMessage());
            }

            verify(recordService, never()).exportRecordsToExcel(anyLong());
        }
    }

    // ===================== exportStudyRecordsByCourse 测试 =====================

    @Test
    void exportStudyRecordsByCourse_Success() throws IOException {
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            // 准备测试数据
            Long studentId = 1L;
            Long courseId = 100L;
            byte[] mockExcelData = "mock course study excel data".getBytes();

            stpUtilMock.when(StpUtil::isLogin).thenReturn(true);
            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(studentId);
            when(recordService.exportStudyRecordsByCourseToExcel(studentId, courseId))
                    .thenReturn(mockExcelData);

            // 执行测试
            recordController.exportStudyRecordsByCourse(courseId, response);

            // 验证
            verify(response).setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            verify(response).setHeader("Content-Disposition", 
                    "attachment; filename=course_" + courseId + "_study_records.xlsx");
            verify(outputStream).write(mockExcelData);
            verify(outputStream).flush();
            verify(recordService, times(1)).exportStudyRecordsByCourseToExcel(studentId, courseId);
        }
    }

    @Test
    void exportStudyRecordsByCourse_NotLoggedIn() throws IOException {
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            Long courseId = 100L;
            stpUtilMock.when(StpUtil::isLogin).thenReturn(false);

            // 执行测试
            recordController.exportStudyRecordsByCourse(courseId, response);

            // 验证错误响应
            verify(response).setContentType("application/json;charset=UTF-8");
            verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
            verify(printWriter).write("{\"code\":400,\"message\":\"请先登录\"}");
            verify(recordService, never()).exportStudyRecordsByCourseToExcel(anyLong(), anyLong());
        }
    }

    // ===================== exportPracticeRecords 测试 =====================

    @Test
    void exportPracticeRecords_Success() throws IOException {
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            // 准备测试数据
            Long studentId = 1L;
            byte[] mockExcelData = "mock practice excel data".getBytes();

            stpUtilMock.when(StpUtil::isLogin).thenReturn(true);
            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(studentId);
            when(recordService.exportPracticeRecordsToExcel(studentId)).thenReturn(mockExcelData);

            // 执行测试
            recordController.exportPracticeRecords(response);

            // 验证
            verify(response).setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            verify(response).setHeader("Content-Disposition", "attachment; filename=practice_record.xlsx");
            verify(response).setHeader("Content-Length", String.valueOf(mockExcelData.length));
            verify(outputStream).write(mockExcelData);
            verify(outputStream).flush();
            verify(recordService, times(1)).exportPracticeRecordsToExcel(studentId);
        }
    }

    @Test
    void exportPracticeRecords_ServiceThrowsException() throws IOException {
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            Long studentId = 1L;
            stpUtilMock.when(StpUtil::isLogin).thenReturn(true);
            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(studentId);
            when(recordService.exportPracticeRecordsToExcel(studentId))
                    .thenThrow(new RuntimeException("导出失败"));

            // 执行测试
            recordController.exportPracticeRecords(response);

            // 验证错误响应
            verify(response).setContentType("application/json;charset=UTF-8");
            verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
            verify(printWriter).write("{\"code\":400,\"message\":\"导出练习记录失败: 导出失败\"}");
        }
    }

    // ===================== exportPracticeRecordsByCourse 测试 =====================

    @Test
    void exportPracticeRecordsByCourse_Success() throws IOException {
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            // 准备测试数据
            Long studentId = 1L;
            Long courseId = 100L;
            byte[] mockExcelData = "mock course practice excel data".getBytes();

            stpUtilMock.when(StpUtil::isLogin).thenReturn(true);
            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(studentId);
            when(recordService.exportPracticeRecordsByCourseToExcel(studentId, courseId))
                    .thenReturn(mockExcelData);

            // 执行测试
            recordController.exportPracticeRecordsByCourse(courseId, response);

            // 验证
            verify(response).setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            verify(response).setHeader("Content-Disposition", 
                    "attachment; filename=course_" + courseId + "_practice_records.xlsx");
            verify(response).setHeader("Content-Length", String.valueOf(mockExcelData.length));
            verify(outputStream).write(mockExcelData);
            verify(outputStream).flush();
            verify(recordService, times(1)).exportPracticeRecordsByCourseToExcel(studentId, courseId);
        }
    }

    @Test
    void exportPracticeRecordsByCourse_EmptyData() throws IOException {
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            Long studentId = 1L;
            Long courseId = 100L;
            stpUtilMock.when(StpUtil::isLogin).thenReturn(true);
            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(studentId);
            when(recordService.exportPracticeRecordsByCourseToExcel(studentId, courseId))
                    .thenReturn(new byte[0]); // 返回空数据

            // 执行测试
            recordController.exportPracticeRecordsByCourse(courseId, response);

            // 验证错误响应
            verify(response).setContentType("application/json;charset=UTF-8");
            verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
            verify(printWriter).write("{\"code\":400,\"message\":\"导出数据为空\"}");
        }
    }

    // ===================== getSubmissionReport 测试 =====================

    @Test
    void getSubmissionReport_Success() throws Exception {
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            // 准备测试数据
            Long studentId = 1L;
            Long submissionId = 200L;
            Map<String, Object> mockReport = new HashMap<>();
            mockReport.put("score", 85);
            mockReport.put("totalQuestions", 10);
            mockReport.put("correctAnswers", 8);

            stpUtilMock.when(StpUtil::isLogin).thenReturn(true);
            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(studentId);
            when(recordService.getSubmissionReport(submissionId, studentId)).thenReturn(mockReport);

            // 执行测试
            mockMvc.perform(get("/api/record/submission/{submissionId}/report", submissionId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.score").value(85))
                    .andExpect(jsonPath("$.data.totalQuestions").value(10));

            verify(recordService, times(1)).getSubmissionReport(submissionId, studentId);
        }
    }

    @Test
    void getSubmissionReport_NotFound() throws Exception {
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            Long studentId = 1L;
            Long submissionId = 999L;

            stpUtilMock.when(StpUtil::isLogin).thenReturn(true);
            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(studentId);
            when(recordService.getSubmissionReport(submissionId, studentId)).thenReturn(null);

            // 执行测试
            mockMvc.perform(get("/api/record/submission/{submissionId}/report", submissionId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpected(jsonPath("$.message").value("未找到该提交记录"));

            verify(recordService, times(1)).getSubmissionReport(submissionId, studentId);
        }
    }

    // ===================== exportSubmissionReport 测试 =====================

    @Test
    void exportSubmissionReport_Success() throws IOException {
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            // 准备测试数据
            Long studentId = 1L;
            Long submissionId = 200L;
            Map<String, Object> mockReport = new HashMap<>();
            mockReport.put("score", 85);
            byte[] mockPdfData = "mock pdf data".getBytes();

            stpUtilMock.when(StpUtil::isLogin).thenReturn(true);
            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(studentId);
            when(recordService.getSubmissionReport(submissionId, studentId)).thenReturn(mockReport);
            when(recordService.generateSubmissionReportPdf(mockReport)).thenReturn(mockPdfData);

            // 执行测试
            recordController.exportSubmissionReport(submissionId, response);

            // 验证
            verify(response).setContentType("application/pdf");
            verify(response).setHeader("Content-Disposition", "attachment; filename=submission_report.pdf");
            verify(outputStream).write(mockPdfData);
            verify(recordService, times(1)).getSubmissionReport(submissionId, studentId);
            verify(recordService, times(1)).generateSubmissionReportPdf(mockReport);
        }
    }

    @Test
    void exportSubmissionReport_ReportNotFound() throws IOException {
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            Long studentId = 1L;
            Long submissionId = 999L;

            stpUtilMock.when(StpUtil::isLogin).thenReturn(true);
            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(studentId);
            when(recordService.getSubmissionReport(submissionId, studentId)).thenReturn(null);

            // 执行测试
            recordController.exportSubmissionReport(submissionId, response);

            // 验证错误响应
            verify(response).setContentType("application/json;charset=UTF-8");
            verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
            verify(printWriter).write("{\"code\":400,\"message\":\"未找到该提交记录\"}");
            verify(recordService, never()).generateSubmissionReportPdf(any());
        }
    }

    // ===================== 辅助方法 =====================

    private StudyRecord createMockStudyRecord(Long id, String courseName) {
        StudyRecord record = new StudyRecord();
        record.setId(id);
        record.setCourseName(courseName);
        record.setStudyTime(120); // 学习时长2小时
        return record;
    }

    private PracticeRecord createMockPracticeRecord(Long id, String practiceName) {
        PracticeRecord record = new PracticeRecord();
        record.setId(id);
        record.setPracticeName(practiceName);
        record.setScore(85);
        return record;
    }
}