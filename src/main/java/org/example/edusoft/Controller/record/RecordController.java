package org.example.edusoft.controller.record;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.example.edusoft.service.record.RecordService;
import org.example.edusoft.common.Result;
import org.example.edusoft.entity.record.*;
import cn.dev33.satoken.stp.StpUtil;

@RestController
@RequestMapping("/api/record")
public class RecordController {

    @Autowired
    private RecordService recordService;

    // 查询所有学习记录
    @GetMapping("/study")
    public Result<List<StudyRecord>> getStudyRecords() {
        if (!StpUtil.isLogin()) {
            return Result.error("请先登录");
        }
        Long studentId = StpUtil.getLoginIdAsLong();
        return Result.success(recordService.getStudyRecords(studentId));
    }

    // 查询某一课的学习记录
    @GetMapping("/study/course/{courseId}")
    public Result<List<StudyRecord>> getStudyRecordsByCourse(@PathVariable Long courseId) {
        if (!StpUtil.isLogin()) {
            return Result.error("请先登录");
        }
        Long studentId = StpUtil.getLoginIdAsLong();
        List<StudyRecord> records = recordService.getStudyRecordsByCourse(studentId, courseId);
        return Result.success(records);
    }

    // 查询所有练习记录
    @GetMapping("/practice")
    public Result<List<PracticeRecord>> getPracticeRecords() {
        // 检查登录状态
        if (!StpUtil.isLogin()) {
            return Result.error("请先登录");
        }
        Long studentId = StpUtil.getLoginIdAsLong();
        return Result.success(recordService.getPracticeRecords(studentId));
    }

    // 查询某一课程练习记录
    @GetMapping("/practice/course/{courseId}")
    public Result<List<PracticeRecord>> getPracticeRecordsByCourse(@PathVariable Long courseId) {
        if (!StpUtil.isLogin()) {
            return Result.error("请先登录");
        }
        Long studentId = StpUtil.getLoginIdAsLong();
        List<PracticeRecord> records = recordService.getPracticeRecordsByCourse(studentId, courseId);
        return Result.success(records);
    }

    // 导出所有学习记录
    @GetMapping("/study/export")
    public void exportRecords(HttpServletResponse response) {
        if (!StpUtil.isLogin()) {
            throw new RuntimeException("请先登录");
        }
        try {
            Long studentId = StpUtil.getLoginIdAsLong();
            byte[] data = recordService.exportRecordsToExcel(studentId);

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=learning_records.xlsx");
            response.getOutputStream().write(data);
        } catch (IOException e) {
            throw new RuntimeException("导出文件失败", e);
        }
    }

    // 导出某一课程的学习记录
    @GetMapping("/study/course/{courseId}/export")
    public void exportStudyRecordsByCourse(@PathVariable Long courseId, HttpServletResponse response) {
        try {
            if (!StpUtil.isLogin()) {
                writeErrorResponse(response, "请先登录");
                return;
            }
            Long studentId = StpUtil.getLoginIdAsLong();
            byte[] data = recordService.exportStudyRecordsByCourseToExcel(studentId, courseId);
            // 设响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment; filename=course_" + courseId + "_study_records.xlsx");
            response.setHeader("Content-Length", String.valueOf(data.length));
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            // 写入响应流
            response.getOutputStream().write(data);
            response.getOutputStream().flush();
        } catch (IOException e) {
            try {
                writeErrorResponse(response, "导出学习记录失败: " + e.getMessage());
            } catch (IOException ex) {
                throw new RuntimeException("响应错误信息失败", ex);
            }
        }
    }
    

    // 导出所有练习记录
    @GetMapping("/practice/export")
    public void exportPracticeRecords(HttpServletResponse response) {
        try {
            // 检查登录状态
            if (!StpUtil.isLogin()) {
                writeErrorResponse(response, "请先登录");
                return;
            }

            Long studentId = StpUtil.getLoginIdAsLong();
            byte[] data = recordService.exportPracticeRecordsToExcel(studentId);

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=practice_record.xlsx");
            response.setHeader("Content-Length", String.valueOf(data.length));
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);

            // 写入响应流
            response.getOutputStream().write(data);
            response.getOutputStream().flush();
        } catch (IOException e) {
            try {
                writeErrorResponse(response, "导出练习记录失败: " + e.getMessage());
            } catch (IOException ex) {
                throw new RuntimeException("响应错误信息失败", ex);
            }
        }
    }

    // 导出某一课程练习记录
    @GetMapping("/practice/course/{courseId}/export")
    public void exportPracticeRecordsByCourse(@PathVariable Long courseId, HttpServletResponse response) {
        try {
            if (!StpUtil.isLogin()) {
                writeErrorResponse(response, "请先登录");
                return;
            }
            Long studentId = StpUtil.getLoginIdAsLong();
            byte[] data = recordService.exportPracticeRecordsByCourseToExcel(studentId, courseId);
            
            if (data == null || data.length == 0) {
                writeErrorResponse(response, "导出数据为空");
                return;
            }

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment; filename=course_" + courseId + "_practice_records.xlsx");
            response.setHeader("Content-Length", String.valueOf(data.length));
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);

            // 写入响应流
            response.getOutputStream().write(data);
            response.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                writeErrorResponse(response, "导出练习记录失败: " + e.getMessage());
            } catch (IOException ex) {
                throw new RuntimeException("响应错误信息失败", ex);
            }
        }
    }

    // 获得某次练习提交的报告
    @GetMapping("/submission/{submissionId}/report")
    public Result<Map<String, Object>> getSubmissionReport(@PathVariable Long submissionId) {
        if (!StpUtil.isLogin()) {
            return Result.error("请先登录");
        }
        try {
            Long studentId = StpUtil.getLoginIdAsLong();
            Map<String, Object> report = recordService.getSubmissionReport(submissionId, studentId);
            if (report == null || report.isEmpty()) {
                return Result.error("未找到该提交记录");
            }
            return Result.success(report);
        } catch (Exception e) {
            return Result.error("获取练习报告失败: " + e.getMessage());
        }
    }

    // 导出某次练习提交的报告
    @GetMapping("/submission/{submissionId}/export-report")
    public void exportSubmissionReport(@PathVariable Long submissionId, HttpServletResponse response) {
        try {
            if (!StpUtil.isLogin()) {
                writeErrorResponse(response, "请先登录");
                return;
            }
            Long studentId = StpUtil.getLoginIdAsLong();
            // 检查提交是否存在
            Map<String, Object> reportData = recordService.getSubmissionReport(submissionId, studentId);
            if (reportData == null || reportData.isEmpty()) {
                writeErrorResponse(response, "未找到该提交记录");
                return;
            }
            // 生成PDF报告
            byte[] pdfData = recordService.generateSubmissionReportPdf(reportData);
            if (pdfData == null || pdfData.length == 0) {
                writeErrorResponse(response, "生成PDF报告失败");
                return;
            }
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=submission_report.pdf");
            response.getOutputStream().write(pdfData);
        } catch (Exception e) {
            try {
                writeErrorResponse(response, "导出报告失败: " + e.getMessage());
            } catch (IOException ex) {
                throw new RuntimeException("响应错误信息失败", ex);
            }
        }
    }
    // 添加辅助方法处理错误响应
    private void writeErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write(String.format("{\"code\":400,\"message\":\"%s\"}", message));
    }
}
