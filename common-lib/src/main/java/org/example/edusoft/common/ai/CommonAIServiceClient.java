package org.example.edusoft.common.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

/**
 * 通用AI服务客户端
 * 提供给各微服务调用AI服务的统一接口
 * 实际的AI功能主要由learning-service提供
 */
@Component
public class CommonAIServiceClient {

    @Value("${learning.service.url:http://localhost:8084}")
    private String learningServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 调用AI助手服务
     * @param question 问题
     * @param context 上下文信息
     * @return AI回答
     */
    public Map<String, Object> askAI(String question, Map<String, Object> context) {
        String url = learningServiceUrl + "/api/learning/ai/rag/assistant";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> request = Map.of(
            "question", question,
            "context", context != null ? context : Map.of()
        );
        
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(request, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            return Map.of(
                "status", "fail",
                "message", "AI服务调用失败: " + e.getMessage()
            );
        }
    }

    /**
     * 调用AI生成练习题 - 简单版本
     * @param courseContent 课程内容
     * @return 生成的练习题
     */
    public Map<String, Object> generateExercise(String courseContent) {
        String url = learningServiceUrl + "/api/learning/ai/rag/generate_exercise";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // 构造符合AI微服务期望的格式
        Map<String, Object> request = Map.of(
            "course_name", "通用课程",
            "lesson_content", courseContent,
            "difficulty", "medium",
            "choose_count", 5,
            "fill_blank_count", 3,
            "question_count", 2
        );
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(request, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            return Map.of(
                "status", "fail",
                "message", "AI练习生成失败: " + e.getMessage()
            );
        }
    }

    /**
     * 调用AI生成练习题 - 完整版本（用于题库页面）
     * @param exerciseRequest 完整的练习生成请求
     * @return 生成的练习题
     */
    public Map<String, Object> generateExercise(Map<String, Object> exerciseRequest) {
        String url = learningServiceUrl + "/api/learning/ai/rag/generate_exercise";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(exerciseRequest, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            return Map.of(
                "status", "fail",
                "message", "AI练习生成失败: " + e.getMessage()
            );
        }
    }

    /**
     * 调用AI评估主观题
     * @param question 题目
     * @param answer 学生答案
     * @param standardAnswer 标准答案
     * @return 评估结果
     */
    public Map<String, Object> evaluateSubjectiveAnswer(String question, String answer, String standardAnswer) {
        String url = learningServiceUrl + "/api/learning/ai/evaluate-subjective";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // 使用符合Python AI微服务期望的字段名
        Map<String, Object> request = Map.of(
            "question", question,
            "student_answer", answer,
            "reference_answer", standardAnswer,
            "max_score", 10.0  // 默认最大分数
        );
        
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(request, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            return Map.of(
                "status", "fail",
                "message", "AI评估失败: " + e.getMessage()
            );
        }
    }

    /**
     * 通用AI服务调用
     * @param endpoint AI服务端点
     * @param request 请求参数
     * @return 响应结果
     */
    public Map<String, Object> callAIService(String endpoint, Map<String, Object> request) {
        String url = learningServiceUrl + "/api/learning/ai/call-service";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> body = Map.of(
            "endpoint", endpoint,
            "request", request
        );
        
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            return Map.of(
                "status", "fail",
                "message", "AI服务调用失败: " + e.getMessage()
            );
        }
    }
}
