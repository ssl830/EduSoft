package org.example.edusoft.learning.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.HashMap;
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
        
        // 使用符合Python AI微服务期望的字段名
        Map<String, Object> body = Map.of(
            "course_outline", courseOutline,
            "course_name", courseName,
            "expected_hours", expectedHours
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
            "reference_answer", referenceAnswer,
            "max_score", 10.0  // 默认最大分数
        );
        return evaluateSubjective(req);
    }

    /**
     * 生成练习题 - 用于SelfPractice
     * 将用户的自然语言prompt转换为符合Python AI微服务要求的格式
     */
    public Map<String, Object> generateExercise(String prompt) {
        String url = aiServiceUrl + "/rag/generate_exercise";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // 解析prompt中的要求，构造符合AI微服务期望的JSON格式
        Map<String, Object> body = parsePromptToExerciseRequest(prompt);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        
        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
        return response.getBody();
    }

    /**
     * 生成练习题 - 完整格式（用于题库页面）
     * 直接转发完整的练习生成请求
     */
    public Map<String, Object> generateExercise(Map<String, Object> exerciseRequest) {
        String url = aiServiceUrl + "/rag/generate_exercise";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(exerciseRequest, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
        
        return response.getBody();
    }
    
    /**
     * 将用户的自然语言prompt解析为AI微服务期望的ExerciseGenerationRequest格式
     */
    private Map<String, Object> parsePromptToExerciseRequest(String prompt) {
        Map<String, Object> body = new HashMap<>();
        
        // 默认值
        body.put("course_name", "通用课程");
        body.put("lesson_content", prompt);
        body.put("difficulty", "medium");
        body.put("choose_count", 5);
        body.put("fill_blank_count", 3);
        body.put("question_count", 2);
        
        // 尝试从prompt中解析特定要求
        String lowerPrompt = prompt.toLowerCase();
        
        // 解析课程名称
        if (lowerPrompt.contains("java")) {
            body.put("course_name", "Java编程基础");
        } else if (lowerPrompt.contains("python")) {
            body.put("course_name", "Python编程");
        } else if (lowerPrompt.contains("数据库")) {
            body.put("course_name", "数据库原理");
        } else if (lowerPrompt.contains("算法")) {
            body.put("course_name", "数据结构与算法");
        }
        
        // 解析难度
        if (lowerPrompt.contains("简单") || lowerPrompt.contains("基础")) {
            body.put("difficulty", "easy");
        } else if (lowerPrompt.contains("困难") || lowerPrompt.contains("高级")) {
            body.put("difficulty", "hard");
        }
        
        // 解析题目数量
        if (lowerPrompt.matches(".*?(\\d+).*?题.*?")) {
            try {
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+).*?题");
                java.util.regex.Matcher matcher = pattern.matcher(lowerPrompt);
                if (matcher.find()) {
                    int count = Integer.parseInt(matcher.group(1));
                    if (lowerPrompt.contains("选择")) {
                        body.put("choose_count", count);
                        body.put("fill_blank_count", 0);
                        body.put("question_count", 0);
                    } else if (lowerPrompt.contains("填空")) {
                        body.put("choose_count", 0);
                        body.put("fill_blank_count", count);
                        body.put("question_count", 0);
                    } else {
                        // 平均分配
                        int chooseCount = count / 2;
                        int fillCount = count / 4;
                        int questionCount = count - chooseCount - fillCount;
                        body.put("choose_count", chooseCount);
                        body.put("fill_blank_count", fillCount);
                        body.put("question_count", Math.max(1, questionCount));
                    }
                }
            } catch (Exception e) {
                // 解析失败，使用默认值
            }
        }
        
        return body;
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
