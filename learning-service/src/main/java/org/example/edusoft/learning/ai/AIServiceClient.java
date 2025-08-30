package org.example.edusoft.learning.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

/**
 * AIServiceClient 封装调用Python AI微服务的HTTP工具类。
 * 支持资料上传、RAG问答、自动生成练习等功能。
 */
@Component
public class AIServiceClient {

    @Value("${ai.service.url:http://localhost:8000}")
    private String aiServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 上传资料并入库（调用/embedding/upload）
     * @param file 要上传的文件
     * @return 上传结果
     * @throws IOException 如果文件读取失败
     */
    public String uploadMaterial(MultipartFile file) throws IOException {
        return uploadMaterial(file, null);
    }

    /**
     * 上传资料并入库（调用/embedding/upload）
     * @param file 要上传的文件
     * @param courseId 关联的课程ID，用于支持联合知识库
     * @return 上传结果
     * @throws IOException 如果文件读取失败
     */
    public String uploadMaterial(MultipartFile file, String courseId) throws IOException {
        String url = aiServiceUrl + "/embedding/upload";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
        if (courseId != null) {
            body.add("course_id", courseId);
        }
        
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        
        return response.getBody();
    }

    /**
     * RAG智能问答（调用/rag/answer）
     */
    public String ragAnswer(String question) {
        String url = aiServiceUrl + "/rag/answer";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> body = Map.of("question", question);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        return response.getBody();
    }

    /**
     * 自动生成练习题与答案（调用/rag/generate_exercise） - 基于课程大纲
     */
    public String generateExerciseFromOutline(String courseOutline) {
        String url = aiServiceUrl + "/rag/generate_exercise";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> body = Map.of("courseOutline", courseOutline);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        return response.getBody();
    }

    /**
     * 根据课程大纲自动生成教学内容（调用/rag/generate_teaching_content）
     */
    public Map<String, Object> generateTeachingContent(String courseOutline, String courseName, Integer expectedHours) {
        String url = aiServiceUrl + "/rag/generate_teaching_content";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> body = Map.of(
            "courseOutline", courseOutline,
            "courseName", courseName,
            "expectedHours", expectedHours
        );
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        
        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
        return response.getBody();
    }

    /**
     * 主观题AI评估
     */
    public Map<String, Object> evaluateSubjective(Map<String, Object> req) {
        String url = aiServiceUrl + "/rag/evaluate_subjective";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(req, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
        
        return response.getBody();
    }

    /**
     * 主观题AI评分 - 用于SelfPractice
     */
    public Map<String, Object> evaluateSubjectiveAnswer(String question, String studentAnswer, String referenceAnswer) {
        Map<String, Object> req = Map.of(
            "question", question,
            "student_answer", studentAnswer,
            "reference_answer", referenceAnswer
        );
        return evaluateSubjective(req);
    }

    /**
     * 生成练习题 - 用于SelfPractice
     */
    public Map<String, Object> generateExercise(String prompt) {
        String url = aiServiceUrl + "/rag/generate_exercise";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> body = Map.of("prompt", prompt);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        
        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
        return response.getBody();
    }

    /**
     * 获取课程优化建议（调用/rag/optimize_course）
     */
    public Map<String, Object> optimizeCourse(String courseData) {
        String url = aiServiceUrl + "/rag/optimize_course";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> body = Map.of("courseData", courseData);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        
        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
        return response.getBody();
    }

    /**
     * 通用AI服务调用方法
     */
    public Map<String, Object> callAiService(String endpoint, Map<String, Object> request) {
        String url = aiServiceUrl + endpoint;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(request, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
        
        return response.getBody();
    }
}
