package org.example.edusoft.learning.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import org.example.edusoft.learning.client.UserServiceClient;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.example.edusoft.learning.service.RecordService;
import org.example.edusoft.learning.Result;
import org.example.edusoft.learning.entity.PracticeRecord;
import org.example.edusoft.learning.entity.StudyRecord;
import org.example.edusoft.learning.entity.QuestionRecord;
import java.util.HashMap;
import org.example.edusoft.learning.mapper.PracticeRecordMapper;

@RestController
@RequestMapping("/api/record")
public class RecordController {

    @Autowired
    private RecordService recordService;

    @Autowired
    private UserServiceClient userClient;

    @Autowired
    private PracticeRecordMapper practiceRecordMapper;

    @Value("${services.user.base-url:http://localhost:8081}")
    private String userServiceBaseUrl;

    // 查询所有学习记录，完成微服务改造
    @GetMapping("/study")
    public Result<List<StudyRecord>> getStudyRecords(HttpServletRequest request) {
        String token = request.getHeader("satoken");
        if (token == null || token.isEmpty()) {
            return Result.error("请先登录");
        }
        Map<String, Object> userInfo = userClient.fetchCurrentUser(userServiceBaseUrl, token);
        if (userInfo == null || userInfo.get("id") == null) {
            return Result.error("请先登录");
        }
        Long studentId = Long.valueOf(userInfo.get("id").toString());
        return Result.success(recordService.getStudyRecords(studentId));
    }

    // 查询某一课的学习记录,完成微服务化改造
    @GetMapping("/study/course/{courseId}")
    public Result<List<StudyRecord>> getStudyRecordsByCourse(@PathVariable Long courseId, HttpServletRequest request) {
        String token = request.getHeader("satoken");
        if (token == null || token.isEmpty()) {
            return Result.error("请先登录");
        }
        Map<String, Object> userInfo = userClient.fetchCurrentUser(userServiceBaseUrl, token);
        if (userInfo == null || userInfo.get("id") == null) {
            return Result.error("请先登录");
        }
        Long studentId = Long.valueOf(userInfo.get("id").toString());
        List<StudyRecord> records = recordService.getStudyRecordsByCourse(studentId, courseId);
        return Result.success(records);
    }

    // 查询所有练习记录
    @GetMapping("/practice")
    public Result<List<PracticeRecord>> getPracticeRecords(HttpServletRequest request) {
        // 检查登录状态
        String token = request.getHeader("satoken");
        if (token == null || token.isEmpty()) {
            return Result.error("请先登录");
        }
        Map<String, Object> userInfo = userClient.fetchCurrentUser(userServiceBaseUrl, token);
        if (userInfo == null || userInfo.get("id") == null) {
            return Result.error("请先登录");
        }
        Long studentId = Long.valueOf(userInfo.get("id").toString());
        List<PracticeRecord> records = recordService.getPracticeRecords(studentId);
        return Result.success(records);
    }

    // 查询某一课程练习记录
    @GetMapping("/practice/course/{courseId}")
    public Result<List<PracticeRecord>> getPracticeRecordsByCourse(@PathVariable Long courseId, HttpServletRequest request) {
        String token = request.getHeader("satoken");
        if (token == null || token.isEmpty()) {
            return Result.error("请先登录");
        }
        Map<String, Object> userInfo = userClient.fetchCurrentUser(userServiceBaseUrl, token);
        if (userInfo == null || userInfo.get("id") == null) {
            return Result.error("请先登录");
        }
        Long studentId = Long.valueOf(userInfo.get("id").toString());
        List<PracticeRecord> records = recordService.getPracticeRecordsByCourse(studentId, courseId);
        return Result.success(records);
    }

    // 导出所有学习记录
    @GetMapping("/study/export")
    public void exportRecords(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("satoken");
        if (token == null || token.isEmpty()) {
            try {
                writeErrorResponse(response, "请先登录");
            } catch (IOException e) {
                throw new RuntimeException("响应错误信息失败", e);
            }
            return;
        }
        Map<String, Object> userInfo = userClient.fetchCurrentUser(userServiceBaseUrl, token);
        if (userInfo == null || userInfo.get("id") == null) {
            try {
                writeErrorResponse(response, "请先登录");
            } catch (IOException e) {
                throw new RuntimeException("响应错误信息失败", e);
            }
            return;
        }
        try {
            Long studentId = Long.valueOf(userInfo.get("id").toString());
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
    public void exportStudyRecordsByCourse(@PathVariable Long courseId, HttpServletRequest request, HttpServletResponse response) {
        try {
            String token = request.getHeader("satoken");
            if (token == null || token.isEmpty()) {
                writeErrorResponse(response, "请先登录");
                return;
            }
            Map<String, Object> userInfo = userClient.fetchCurrentUser(userServiceBaseUrl, token);
            if (userInfo == null || userInfo.get("id") == null) {
                writeErrorResponse(response, "请先登录");
                return;
            }
            Long studentId = Long.valueOf(userInfo.get("id").toString());
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
    public void exportPracticeRecords(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 检查登录状态
            String token = request.getHeader("satoken");
            if (token == null || token.isEmpty()) {
                writeErrorResponse(response, "请先登录");
                return;
            }

            Map<String, Object> userInfo = userClient.fetchCurrentUser(userServiceBaseUrl, token);
            if (userInfo == null || userInfo.get("id") == null) {
                writeErrorResponse(response, "请先登录");
                return;
            }
            Long studentId = Long.valueOf(userInfo.get("id").toString());
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
    public void exportPracticeRecordsByCourse(@PathVariable Long courseId, HttpServletRequest request, HttpServletResponse response) {
        try {
            String token = request.getHeader("satoken");
            if (token == null || token.isEmpty()) {
                writeErrorResponse(response, "请先登录");
                return;
            }

            Map<String, Object> userInfo = userClient.fetchCurrentUser(userServiceBaseUrl, token);
            if (userInfo == null || userInfo.get("id") == null) {
                writeErrorResponse(response, "请先登录");
                return;
            }
            Long studentId = Long.valueOf(userInfo.get("id").toString());
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
    public Result<Map<String, Object>> getSubmissionReport(@PathVariable Long submissionId, HttpServletRequest request) {
        System.out.println("=== Controller getSubmissionReport 开始 ===");
        System.out.println("submissionId: " + submissionId);
        
        try {
            String token = request.getHeader("satoken");
            System.out.println("Token: " + (token != null ? "存在" : "不存在"));
            
            if (token == null || token.isEmpty()) {
                System.out.println("Token为空，返回请先登录");
                return Result.error("请先登录");
            }

            System.out.println("正在获取用户信息...");
            Map<String, Object> userInfo = userClient.fetchCurrentUser(userServiceBaseUrl, token);
            System.out.println("用户信息: " + userInfo);
            
            if (userInfo == null || userInfo.get("id") == null) {
                System.out.println("用户信息无效，返回请先登录");
                return Result.error("请先登录");
            }
            
            Long studentId = Long.valueOf(userInfo.get("id").toString());
            System.out.println("studentId: " + studentId);
            
            System.out.println("正在调用Service层...");
            Map<String, Object> report = recordService.getSubmissionReport(submissionId, studentId);
            System.out.println("Service层返回结果: " + (report != null ? "有数据" : "null"));
            
            if (report == null || report.isEmpty()) {
                System.out.println("报告为空，返回未找到该提交记录");
                return Result.error("未找到该提交记录");
            }
            
            System.out.println("报告获取成功，返回成功结果");
            return Result.success(report);
        } catch (Exception e) {
            System.out.println("=== Controller getSubmissionReport 出现异常 ===");
            System.out.println("异常类型: " + e.getClass().getName());
            System.out.println("异常信息: " + e.getMessage());
            e.printStackTrace();
            return Result.error("获取练习报告失败: " + e.getMessage());
        }
    }

    // 导出某次练习提交的报告
    @GetMapping("/submission/{submissionId}/export-report")
    public void exportSubmissionReport(@PathVariable Long submissionId, HttpServletRequest request, HttpServletResponse response) {
        try {
            String token = request.getHeader("satoken");
            if (token == null || token.isEmpty()) {
                writeErrorResponse(response, "请先登录");
                return;
            }

            Map<String, Object> userInfo = userClient.fetchCurrentUser(userServiceBaseUrl, token);
            if (userInfo == null || userInfo.get("id") == null) {
                writeErrorResponse(response, "请先登录");
                return;
            }
            Long studentId = Long.valueOf(userInfo.get("id").toString());
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

    // 调试接口：检查练习记录数据
    @GetMapping("/debug/practice/{courseId}")
    public Result<Map<String, Object>> debugPracticeRecords(@PathVariable Long courseId, HttpServletRequest request) {
        try {
            String token = request.getHeader("satoken");
            if (token == null || token.isEmpty()) {
                return Result.error("请先登录");
            }

            Map<String, Object> userInfo = userClient.fetchCurrentUser(userServiceBaseUrl, token);
            if (userInfo == null || userInfo.get("id") == null) {
                return Result.error("请先登录");
            }
            Long studentId = Long.valueOf(userInfo.get("id").toString());
            
            // 检查数据库中的数据
            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("studentId", studentId);
            debugInfo.put("courseId", courseId);
            
            // 检查submission表
            try {
                List<Map<String, Object>> submissions = practiceRecordMapper.debugFindSubmissions(studentId, courseId);
                debugInfo.put("submissions", submissions);
                debugInfo.put("submissionCount", submissions != null ? submissions.size() : 0);
            } catch (Exception e) {
                debugInfo.put("submissionError", e.getMessage());
            }
            
            // 检查practice表
            try {
                List<Map<String, Object>> practices = practiceRecordMapper.debugFindPractices(courseId);
                debugInfo.put("practices", practices);
                debugInfo.put("practiceCount", practices != null ? practices.size() : 0);
            } catch (Exception e) {
                debugInfo.put("practiceError", e.getMessage());
            }
            
            return Result.success(debugInfo);
        } catch (Exception e) {
            return Result.error("调试失败: " + e.getMessage());
        }
    }

    // 调试接口：检查提交记录数据
    @GetMapping("/debug/submission/{submissionId}")
    public Result<Map<String, Object>> debugSubmissionRecord(@PathVariable Long submissionId, HttpServletRequest request) {
        try {
            String token = request.getHeader("satoken");
            if (token == null || token.isEmpty()) {
                return Result.error("请先登录");
            }

            Map<String, Object> userInfo = userClient.fetchCurrentUser(userServiceBaseUrl, token);
            if (userInfo == null || userInfo.get("id") == null) {
                return Result.error("请先登录");
            }
            Long studentId = Long.valueOf(userInfo.get("id").toString());
            
            // 检查数据库中的数据
            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("submissionId", submissionId);
            debugInfo.put("studentId", studentId);
            
            // 检查submission表
            try {
                PracticeRecord submission = practiceRecordMapper.findSubmissionDetail(submissionId, studentId);
                if (submission != null) {
                    debugInfo.put("submission", submission);
                    debugInfo.put("submissionFound", true);
                    
                    // 检查题目信息
                    try {
                        List<QuestionRecord> questions = practiceRecordMapper.findQuestionsBySubmissionId(submissionId);
                        debugInfo.put("questions", questions);
                        debugInfo.put("questionCount", questions != null ? questions.size() : 0);
                    } catch (Exception e) {
                        debugInfo.put("questionError", e.getMessage());
                    }
                } else {
                    debugInfo.put("submissionFound", false);
                }
            } catch (Exception e) {
                debugInfo.put("submissionError", e.getMessage());
            }
            
            // 检查所有submission记录
            try {
                List<Map<String, Object>> allSubmissions = practiceRecordMapper.debugFindAllSubmissions(studentId);
                debugInfo.put("allSubmissions", allSubmissions);
                debugInfo.put("totalSubmissionCount", allSubmissions != null ? allSubmissions.size() : 0);
            } catch (Exception e) {
                debugInfo.put("allSubmissionError", e.getMessage());
            }
            
            return Result.success(debugInfo);
        } catch (Exception e) {
            return Result.error("调试失败: " + e.getMessage());
        }
    }
}
