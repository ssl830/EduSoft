package org.example.edusoft.learning.controller.ai;

import org.example.edusoft.learning.service.ai.AiAssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/learning/ai")
public class AiAssistantController {
    private static final Logger logger = LoggerFactory.getLogger(AiAssistantController.class);

    @Autowired
    private AiAssistantService aiAssistantService;

    @PostMapping("/embedding/upload")
    public Map<String, Object> uploadEmbeddingFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "course_id", required = false) String courseId
    ) {
        try {
            logger.info("收到文件上传请求: {}, 课程ID: {}", file.getOriginalFilename(), courseId);
            return aiAssistantService.uploadEmbeddingFile(file, courseId);
        } catch (Exception e) {
            logger.error("文件上传处理失败", e);
            return Map.of(
                "status", "fail",
                "message", "文件上传处理失败: " + e.getMessage()
            );
        }
    }

    @PostMapping("/rag/generate")
    public Map<String, Object> generateTeachingContent(@RequestBody Map<String, Object> req) {
        logger.info("收到生成教案请求: {}", req);
        return aiAssistantService.generateTeachingContent(req);
    }

    @PostMapping("/rag/generate_exercise")
    public Map<String, Object> generateExercises(@RequestBody Map<String, Object> req) {
        logger.info("收到生成题目请求: {}", req);
        
        // 前端发送的是完整的练习生成参数，直接转发给AI微服务
        // 包含: course_name, lesson_content, difficulty, choose_count, fill_blank_count, question_count, custom_types
        return aiAssistantService.generateExercises(req);
    }

    @PostMapping("/rag/assistant")
    public Map<String, Object> onlineAssistant(@RequestBody Map<String, Object> req) {
        logger.info("收到在线学习助手请求: {}", req);
        return aiAssistantService.onlineAssistant(req);
    }
    
    @PostMapping("/evaluate-subjective")
    public Map<String, Object> evaluateSubjective(@RequestBody Map<String, Object> req) {
        logger.info("收到主观题AI评估请求: {}", req);
        return aiAssistantService.evaluateSubjective(req);
    }

    @PostMapping("/call-service")
    public Map<String, Object> callAiService(@RequestBody Map<String, Object> req) {
        String endpoint = (String) req.get("endpoint");
        Map<String, Object> request = (Map<String, Object>) req.get("request");
        
        logger.info("收到AI服务调用请求: endpoint={}", endpoint);
        return aiAssistantService.callAiServiceDirectly(endpoint, request);
    }

    @PostMapping("/rag/detail")
    public Map<String, Object> generateTeachingContentDetail(@RequestBody Map<String, Object> req) {
        logger.info("收到生成教案细节请求: {}", req);
        return aiAssistantService.generateTeachingContentDetail(req);
    }

    @PostMapping("/rag/regenerate")
    public Map<String, Object> regenerateTeachingContent(@RequestBody Map<String, Object> req) {
        logger.info("收到重新生成教案请求: {}", req);
        return aiAssistantService.regenerateTeachingContent(req);
    }

    @PostMapping("/rag/generate_student_exercise")
    public Map<String, Object> generateStudentExercise(@RequestBody Map<String, Object> req, 
            @RequestHeader(value = "free-fs-token", required = false) String token,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        logger.info("收到学生自测练习生成请求: {}, Token: {}, 用户ID: {}", req, token != null ? "存在" : "无", userId);
        
        if (userId == null) {
            if (token != null && !token.trim().isEmpty()) {
                // 这里需要解析 token 获取用户ID
                // 暂时返回一个测试用户ID
                userId = 1L; // TODO: 从 token 中解析真实的用户ID
                logger.info("从 token 解析到用户ID: {}", userId);
            } else {
                logger.warn("没有提供有效的用户认证信息");
                return Map.of("status", "fail", "message", "用户未登录");
            }
        }
        
        return aiAssistantService.generateStudentExercise(req, userId);
    }

    @PostMapping("/rag/optimize_course")
    public Map<String, Object> optimizeCourse(@RequestBody Map<String, Object> req) {
        logger.info("收到课程优化建议请求: {}", req);
        return aiAssistantService.optimizeCourse(req);
    }

    @PostMapping("/rag/feedback")
    public Map<String, Object> reviseTeachingContent(@RequestBody Map<String, Object> req) {
        logger.info("收到教案反馈修改请求: {}", req);
        return aiAssistantService.reviseTeachingContent(req);
    }

    @PostMapping("/rag/step_detail")
    public Map<String, Object> generateStepDetail(@RequestBody Map<String, Object> req) {
        logger.info("收到课时环节细节生成请求: {}", req);
        return aiAssistantService.generateStepDetail(req);
    }

    @PostMapping(value = "/rag/generate_section", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> generateSectionTeachingContent(
            @RequestPart("file") MultipartFile file,
            @RequestParam("course_name") String courseName,
            @RequestParam("section_title") String sectionTitle,
            @RequestParam("expected_hours") Integer expectedHours,
            @RequestParam(value = "constraints", required = false) String constraints
    ) {
        logger.info("收到章节教案生成请求: course={}, section={}", courseName, sectionTitle);
        return aiAssistantService.generateSectionTeachingContent(file, courseName, sectionTitle, expectedHours, constraints);
    }

    @PostMapping("/rag/generate_selected_student_exercise")
    public Map<String, Object> generateSelectedStudentExercise(@RequestBody Map<String, Object> req) {
        logger.info("收到学生选题自测练习生成请求: {}", req);
        return aiAssistantService.generateSelectedStudentExercise(req);
    }

    @PostMapping("/analyze-exercise")
    public Map<String, Object> analyzeExercise(@RequestBody Map<String, Object> req) {
        logger.info("收到学情分析请求: {}", req);
        Long practiceId = req.get("practiceId") instanceof Number ? ((Number) req.get("practiceId")).longValue() : null;
        if (practiceId == null) {
            return Map.of("status", "fail", "message", "缺少practiceId");
        }
        return aiAssistantService.analyzeExercise(practiceId);
    }

    @PostMapping("/video/summary")
    public Map<String, Object> generateVideoSummary(@RequestBody Map<String, Object> req) {
        logger.info("收到视频摘要生成请求: {}", req);
        return aiAssistantService.generateVideoSummary(req);
    }

    @PostMapping("/video/summary/text")
    public Map<String, Object> generateTextSummary(@RequestBody Map<String, Object> req) {
        logger.info("收到文本摘要生成请求: {}", req);
        return aiAssistantService.generateTextSummary(req);
    }

    // -------------------- 存储路径管理 --------------------
    @PostMapping("/storage/base_path")
    public Map<String, Object> setStorageBasePath(@RequestBody Map<String, Object> body) {
        String basePath = (String) body.get("base_path");
        logger.info("收到设置存储基础路径请求: {}", basePath);
        return aiAssistantService.setStorageBasePath(basePath);
    }

    @PostMapping("/embedding/base_path")
    public Map<String, Object> setEmbeddingBasePath(@RequestBody Map<String, Object> body) {
        String basePath = (String) body.get("base_path");
        logger.info("收到设置embedding基础路径请求: {}", basePath);
        return aiAssistantService.setEmbeddingBasePath(basePath);
    }

    @PostMapping("/storage/base_path/reset")
    public Map<String, Object> resetStorageBasePath() {
        logger.info("收到重置存储基础路径请求");
        return aiAssistantService.resetStorageBasePath();
    }

    @PostMapping("/embedding/base_path/reset")
    public Map<String, Object> resetEmbeddingBasePath() {
        logger.info("收到重置embedding基础路径请求");
        return aiAssistantService.resetEmbeddingBasePath();
    }

    @GetMapping("/storage/document_exists")
    public Map<String, Object> checkDocumentExists(
            @RequestParam("filename") String filename,
            @RequestParam(value = "course_id", required = false) String courseId
    ) {
        logger.info("收到检查文档存在请求: filename={}, course_id={}", filename, courseId);
        return aiAssistantService.checkDocumentExists(filename, courseId);
    }

    @GetMapping("/storage/list")
    public Map<String, Object> listStoragePaths() {
        logger.info("收到获取存储路径列表请求");
        return aiAssistantService.listStoragePaths();
    }

    @PostMapping("/storage/selected")
    public Map<String, Object> setSelectedStoragePaths(@RequestBody java.util.List<String> paths) {
        logger.info("收到设置选定存储路径请求: {}", paths);
        return aiAssistantService.setSelectedStoragePaths(paths);
    }

    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        logger.info("收到AI服务健康检查请求");
        return aiAssistantService.healthCheck();
    }
}
