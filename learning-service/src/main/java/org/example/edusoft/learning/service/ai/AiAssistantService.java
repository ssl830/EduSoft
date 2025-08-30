package org.example.edusoft.learning.service.ai;

import org.example.edusoft.learning.ai.AIServiceClient;
import org.example.edusoft.learning.entity.ai.AiServiceCallLog;
import org.example.edusoft.learning.mapper.ai.AiServiceCallLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AiAssistantService implements AiServiceCaller {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String aiServiceUrl = "http://localhost:8000"; // Python 微服务地址
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AiServiceCallLogMapper aiServiceCallLogMapper;

    @Autowired
    private AIServiceClient aiServiceClient;

    // Helper method to log AI service calls
    private void logAiServiceCall(Long userId, String endpoint, long durationMs, String status, String errorMessage) {
        AiServiceCallLog log = new AiServiceCallLog();
        log.setUserId(userId);
        log.setEndpoint(endpoint);
        log.setDurationMs(durationMs);
        log.setCallTime(LocalDateTime.now());
        log.setStatus(status);
        log.setErrorMessage(errorMessage);
        aiServiceCallLogMapper.insertLog(log);
    }

    private String buildUrl(String endpoint) {
        return aiServiceUrl + endpoint;
    }

    public Map<String, Object> uploadEmbeddingFile(MultipartFile file, String courseId) {
        long startTime = System.currentTimeMillis();
        String endpoint = "/embedding/upload";
        Long userId = getCurrentUserId();
        
        try {
            // 创建临时文件
            Path tempFile = Files.createTempFile("upload_", "_" + file.getOriginalFilename());
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            String url = buildUrl(endpoint);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(tempFile.toFile()));
            if (courseId != null) {
                body.add("course_id", courseId);
            }

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            Files.deleteIfExists(tempFile);

            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "success", null);
            return response.getBody();
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "fail", e.getMessage());
            return Map.of(
                "status", "fail",
                "message", "AI服务调用失败: " + e.getMessage()
            );
        }
    }

    public Map<String, Object> generateTeachingContent(Map<String, Object> req) {
        long startTime = System.currentTimeMillis();
        String endpoint = "/rag/generate";
        Long userId = getCurrentUserId();
        
        try {
            String url = buildUrl(endpoint);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(req, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);

            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "success", null);
            return response.getBody();
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "fail", e.getMessage());
            return Map.of(
                "status", "fail",
                "message", "AI教案生成服务调用失败: " + e.getMessage()
            );
        }
    }

    public Map<String, Object> generateExercises(Map<String, Object> req) {
        long startTime = System.currentTimeMillis();
        String endpoint = "/rag/generate_exercise";
        Long userId = getCurrentUserId();
        
        try {
            String url = buildUrl(endpoint);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(req, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);

            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "success", null);
            return response.getBody();
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "fail", e.getMessage());
            return Map.of(
                "status", "fail",
                "message", "AI题目生成服务调用失败: " + e.getMessage()
            );
        }
    }

    public Map<String, Object> onlineAssistant(Map<String, Object> req) {
        long startTime = System.currentTimeMillis();
        String endpoint = "/rag/assistant";
        Long userId = getCurrentUserId();
        
        try {
            String url = buildUrl(endpoint);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(req, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            
            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "success", null);
            return response.getBody();
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "fail", e.getMessage());
            return Map.of(
                "status", "fail",
                "message", "AI在线助手服务调用失败: " + e.getMessage()
            );
        }
    }

    public Map<String, Object> evaluateSubjective(Map<String, Object> req) {
        long startTime = System.currentTimeMillis();
        String endpoint = "/rag/evaluate_subjective";
        Long userId = getCurrentUserId();
        
        try {
            String url = buildUrl(endpoint);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(req, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);

            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "success", null);
            return response.getBody();
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "fail", e.getMessage());
            return Map.of(
                "status", "fail",
                "message", "AI主观题评估服务调用失败: " + e.getMessage()
            );
        }
    }

    @Override
    public Map<String, Object> callAiServiceDirectly(String endpoint, Map<String, Object> request) {
        long startTime = System.currentTimeMillis();
        Long userId = getCurrentUserId();
        
        try {
            String url = buildUrl(endpoint);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(request, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);

            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "success", null);
            return response.getBody();
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "fail", e.getMessage());
            return Map.of(
                "status", "fail",
                "message", "AI服务调用失败: " + e.getMessage()
            );
        }
    }

    // 获取当前用户ID的方法，需要根据实际的权限框架实现
    private Long getCurrentUserId() {
        // TODO: 实现获取当前登录用户ID的逻辑
        // 可以通过SecurityContextHolder或其他方式获取
        return null;
    }
}
