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
}
