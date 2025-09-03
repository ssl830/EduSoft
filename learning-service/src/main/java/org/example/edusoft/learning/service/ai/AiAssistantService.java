package org.example.edusoft.learning.service.ai;

import org.example.edusoft.learning.ai.AIServiceClient;
import org.example.edusoft.learning.client.CourseClient;
import org.example.edusoft.learning.client.UserServiceClient;
import org.example.edusoft.learning.client.ContentClient;
import org.example.edusoft.learning.entity.Practice;
import org.example.edusoft.learning.entity.ai.AiServiceCallLog;
import org.example.edusoft.learning.mapper.ai.AiServiceCallLogMapper;
import org.example.edusoft.learning.mapper.PracticeMapper;
import org.example.edusoft.learning.mapper.PracticeQuestionStatMapper;
import org.example.edusoft.learning.mapper.QuestionMapper;
import org.example.edusoft.learning.mapper.SubmissionMapper;
import org.example.edusoft.learning.service.SelfPracticeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AiAssistantService implements AiServiceCaller {
    private static final Logger logger = LoggerFactory.getLogger(AiAssistantService.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final String aiServiceUrl = "http://localhost:8000"; // Python 微服务地址
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AiServiceCallLogMapper aiServiceCallLogMapper;

    @Autowired
    private AIServiceClient aiServiceClient;

    @Autowired
    private CourseClient courseClient;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private ContentClient contentClient;

    @Autowired
    private PracticeMapper practiceMapper;

    @Autowired
    private PracticeQuestionStatMapper practiceQuestionStatMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private SubmissionMapper submissionMapper;

    @Autowired
    private SelfPracticeService selfPracticeService;

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
            // 丰富请求数据 - 从其他微服务获取课程信息
            enrichWithCourseData(req);
            
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
            // 丰富请求数据 - 从其他微服务获取课程和章节信息
            enrichWithCourseData(req);
            
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

    public Map<String, Object> generateTeachingContentDetail(Map<String, Object> req) {
        return callAiServiceMethod("/rag/detail", req);
    }

    public Map<String, Object> regenerateTeachingContent(Map<String, Object> req) {
        return callAiServiceMethod("/rag/regenerate", req);
    }

    public Map<String, Object> generateStudentExercise(Map<String, Object> req, Long userId) {
        if (userId == null) {
            return Map.of("status", "fail", "message", "用户未登录");
        }

        try {
            // 1. 调用AI服务生成练习题
            Map<String, Object> aiResult = callAiServiceMethod("/rag/generate_student_exercise", req);
            
            logger.info("AI服务返回结果: {}", aiResult);
            
            // 检查AI服务是否成功调用
            if (aiResult == null) {
                logger.error("AI服务返回null结果");
                return Map.of("status", "fail", "message", "AI生成练习失败");
            }
            
            // 检查是否包含习题数据 - 根据实际返回格式调整
            // AI服务可能返回 {exercises: [...]} 或 {data: {exercises: [...]}} 格式
            boolean hasExercises = false;
            if (aiResult.containsKey("exercises") && aiResult.get("exercises") instanceof List) {
                hasExercises = !((List<?>) aiResult.get("exercises")).isEmpty();
            } else if (aiResult.containsKey("data") && aiResult.get("data") instanceof Map) {
                Map<?, ?> data = (Map<?, ?>) aiResult.get("data");
                if (data.containsKey("exercises") && data.get("exercises") instanceof List) {
                    hasExercises = !((List<?>) data.get("exercises")).isEmpty();
                }
            }
            
            if (!hasExercises) {
                logger.error("AI服务返回结果中没有有效的习题数据: {}", aiResult);
                return Map.of("status", "fail", "message", "AI生成练习失败");
            }

            // 2. 保存生成的练习到数据库（直接传递AI结果）
            Long practiceId = selfPracticeService.saveGeneratedPractice(userId, aiResult);

            if (practiceId == null) {
                return Map.of("status", "fail", "message", "保存练习到数据库失败");
            }

            // 3. 构造返回结果，包含practiceId
            Map<String, Object> result = new HashMap<>(aiResult);
            result.put("practiceId", practiceId);
            result.put("status", "success");
            result.put("message", "练习生成并保存成功");
            
            logger.info("AI自测练习生成成功 - 用户ID: {}, 练习ID: {}", userId, practiceId);
            return result;
            
        } catch (Exception e) {
            logger.error("生成学生自测练习失败", e);
            return Map.of("status", "fail", "message", "生成练习失败: " + e.getMessage());
        }
    }

    public Map<String, Object> optimizeCourse(Map<String, Object> req) {
        return callAiServiceMethod("/rag/optimize_course", req);
    }

    public Map<String, Object> reviseTeachingContent(Map<String, Object> req) {
        return callAiServiceMethod("/rag/feedback", req);
    }

    public Map<String, Object> generateStepDetail(Map<String, Object> req) {
        return callAiServiceMethod("/rag/step_detail", req);
    }

    public Map<String, Object> generateSectionTeachingContent(MultipartFile file, String courseName, 
            String sectionTitle, Integer expectedHours, String constraints) {
        long startTime = System.currentTimeMillis();
        String endpoint = "/rag/generate_section";
        Long userId = getCurrentUserId();
        
        try {
            // 创建临时文件
            Path tempFile = Files.createTempFile("section_", "_" + file.getOriginalFilename());
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            String url = buildUrl(endpoint);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(tempFile.toFile()));
            body.add("course_name", courseName);
            body.add("section_title", sectionTitle);
            body.add("expected_hours", expectedHours);
            if (constraints != null) {
                body.add("constraints", constraints);
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
                "message", "AI章节教案生成服务调用失败: " + e.getMessage()
            );
        }
    }

    public Map<String, Object> generateSelectedStudentExercise(Map<String, Object> req) {
        return callAiServiceMethod("/rag/generate_selected_student_exercise", req);
    }

    public Map<String, Object> analyzeExercise(Long practiceId) {
        long startTime = System.currentTimeMillis();
        String endpoint = "/rag/analyze_exercise";
        Long userId = getCurrentUserId();
        
        try {
            // 1. 查询所有题目统计信息
            List<Map<String, Object>> statList = practiceQuestionStatMapper.getPracticeQuestionStats(practiceId);
            if (statList == null || statList.isEmpty()) {
                return Map.of("status", "fail", "message", "未找到练习题目");
            }
            
            // 2. 组装参数
            List<Map<String, Object>> exerciseQuestions = new ArrayList<>();
            for (Map<String, Object> stat : statList) {
                Map<String, Object> q = new HashMap<>();
                q.put("content", stat.getOrDefault("content", ""));
                Double scoreRate = stat.get("score_rate") instanceof Number ? ((Number)stat.get("score_rate")).doubleValue() : null;
                double errorRate = 1.0;
                if (scoreRate != null) {
                    errorRate = 1 - scoreRate;
                }
                q.put("error_rate", errorRate);
                q.put("type", stat.getOrDefault("type", ""));
                q.put("score", stat.get("score"));
                q.put("student_count", stat.get("student_count"));
                q.put("correct_count", stat.get("correct_count"));
                q.put("additional_info", null);
                exerciseQuestions.add(q);
            }
            Map<String, Object> req = new HashMap<>();
            req.put("exercise_questions", exerciseQuestions);

            // 调试输出：打印传给AI微服务的请求体
            logger.debug("[AI调试] analyzeExercise 请求体: {}", req);

            // 3. 调用微服务分析
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
            return Map.of("status", "fail", "message", "AI学情分析服务调用失败: " + e.getMessage());
        }
    }

    public Map<String, Object> generateVideoSummary(Map<String, Object> req) {
        return callAiServiceMethod("/video/summary", req);
    }

    public Map<String, Object> generateTextSummary(Map<String, Object> req) {
        return callAiServiceMethod("/video/summary/text", req);
    }

    public Map<String, Object> setStorageBasePath(String basePath) {
        Map<String, Object> req = Map.of("base_path", basePath);
        return callAiServiceMethod("/storage/base_path", req);
    }

    public Map<String, Object> setEmbeddingBasePath(String basePath) {
        Map<String, Object> req = Map.of("base_path", basePath);
        return callAiServiceMethod("/embedding/base_path", req);
    }

    public Map<String, Object> resetStorageBasePath() {
        return callAiServiceMethod("/storage/base_path/reset", new HashMap<>());
    }

    public Map<String, Object> resetEmbeddingBasePath() {
        return callAiServiceMethod("/embedding/base_path/reset", new HashMap<>());
    }

    public Map<String, Object> checkDocumentExists(String filename, String courseId) {
        String endpoint = "/storage/document_exists";
        String url = buildUrl(endpoint) + "?filename=" + filename;
        if (courseId != null) {
            url += "&course_id=" + courseId;
        }
        
        long startTime = System.currentTimeMillis();
        Long userId = getCurrentUserId();
        
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "success", null);
            return response.getBody();
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "fail", e.getMessage());
            return Map.of(
                "status", "fail",
                "message", "AI文档检查服务调用失败: " + e.getMessage()
            );
        }
    }

    public Map<String, Object> listStoragePaths() {
        String endpoint = "/storage/list";
        String url = buildUrl(endpoint);
        
        long startTime = System.currentTimeMillis();
        Long userId = getCurrentUserId();
        
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "success", null);
            return response.getBody();
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "fail", e.getMessage());
            return Map.of(
                "status", "fail",
                "message", "AI存储路径列表服务调用失败: " + e.getMessage()
            );
        }
    }

    public Map<String, Object> setSelectedStoragePaths(java.util.List<String> paths) {
        return callAiServiceMethod("/storage/selected", paths);
    }

    public Map<String, Object> healthCheck() {
        String endpoint = "/health";
        String url = buildUrl(endpoint);
        
        long startTime = System.currentTimeMillis();
        Long userId = getCurrentUserId();
        
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "success", null);
            return response.getBody();
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "fail", e.getMessage());
            return Map.of(
                "status", "fail",
                "message", "AI健康检查服务调用失败: " + e.getMessage()
            );
        }
    }

    // 通用的AI服务调用方法
    private Map<String, Object> callAiServiceMethod(String endpoint, Object request) {
        long startTime = System.currentTimeMillis();
        Long userId = getCurrentUserId();
        
        try {
            String url = buildUrl(endpoint);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> requestEntity = new HttpEntity<>(request, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);

            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "success", null);
            return response.getBody();
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "fail", e.getMessage());
            return Map.of(
                "status", "fail",
                "message", "AI服务调用失败 [" + endpoint + "]: " + e.getMessage()
            );
        }
    }

    /**
     * 丰富请求数据 - 从其他微服务获取课程和章节信息
     */
    private void enrichWithCourseData(Map<String, Object> req) {
        try {
            // 获取课程信息
            if (req.containsKey("course_id")) {
                Object courseIdObj = req.get("course_id");
                Long courseId = null;
                if (courseIdObj instanceof Number) {
                    courseId = ((Number) courseIdObj).longValue();
                } else if (courseIdObj instanceof String) {
                    try {
                        courseId = Long.parseLong((String) courseIdObj);
                    } catch (NumberFormatException e) {
                        logger.warn("Invalid course_id format: {}", courseIdObj);
                        return;
                    }
                }
                
                if (courseId != null) {
                    try {
                        logger.info("Fetching course info for course_id: {}", courseId);
                        Map<String, Object> result = courseClient.getCourseById(courseId);
                        logger.info("Raw course service response: {}", result);
                        
                        if (result != null && result.get("data") != null) {
                            Map<String, Object> courseInfo = (Map<String, Object>) result.get("data");
                            logger.info("Course data: {}", courseInfo);
                            
                            // 添加课程名称（如果请求中没有）
                            if (!req.containsKey("course_name")) {
                                Object courseName = courseInfo.get("name");  // CourseDetailDTO 使用 name 字段
                                if (courseName != null) {
                                    req.put("course_name", courseName);
                                    logger.info("Added course_name: {}", courseName);
                                } else {
                                    logger.warn("No course name found in course data: {}", courseInfo.keySet());
                                }
                            }
                            
                            // 添加课程大纲（如果请求中没有）
                            if (!req.containsKey("course_outline")) {
                                Object courseOutline = courseInfo.get("outline");  // CourseDetailDTO 使用 outline 字段
                                if (courseOutline != null && !courseOutline.toString().trim().isEmpty()) {
                                    req.put("course_outline", courseOutline);
                                    logger.info("Added course_outline: {}", courseOutline);
                                } else {
                                    // 如果没有课程大纲，使用课程目标或默认值
                                    Object objective = courseInfo.get("objective");
                                    if (objective != null && !objective.toString().trim().isEmpty()) {
                                        req.put("course_outline", objective);
                                        logger.info("Added course_outline from objective: {}", objective);
                                    } else {
                                        req.put("course_outline", "暂无课程大纲");
                                        logger.info("Added default course_outline");
                                    }
                                }
                            }
                            
                            // 添加课程描述和其他有用信息
                            Object objective = courseInfo.get("objective");
                            if (objective != null) {
                                req.put("course_objective", objective);
                            }
                            
                            Object assessment = courseInfo.get("assessment");
                            if (assessment != null) {
                                req.put("course_assessment", assessment);
                            }
                        } else {
                            logger.warn("Course service returned null or no data for course_id: {}, response: {}", courseId, result);
                        }
                    } catch (Exception e) {
                        logger.warn("Failed to enrich request data from other services: {}", e.getMessage());
                        // 不抛出异常，继续处理其他信息
                        throw new RuntimeException("调用course-service服务失败: " + e.getMessage(), e);
                    }
                }
            }
            
            // 获取章节信息
            if (req.containsKey("section_id")) {
                Object sectionIdObj = req.get("section_id");
                Long sectionId = null;
                if (sectionIdObj instanceof Number) {
                    sectionId = ((Number) sectionIdObj).longValue();
                } else if (sectionIdObj instanceof String) {
                    try {
                        sectionId = Long.parseLong((String) sectionIdObj);
                    } catch (NumberFormatException e) {
                        logger.warn("Invalid section_id format: {}", sectionIdObj);
                        return;
                    }
                }
                
                if (sectionId != null) {
                    try {
                        logger.info("Fetching section info for section_id: {}", sectionId);
                        logger.info("Calling URL: /api/courses/{}", sectionId);
                        Map<String, Object> result = courseClient.getSectionById(sectionId);
                        logger.info("Raw section service response: {}", result);
                        
                        if (result != null && result.get("data") != null) {
                            Map<String, Object> sectionInfo = (Map<String, Object>) result.get("data");
                            logger.info("Section data: {}", sectionInfo);
                            
                            // 添加章节内容作为 lesson_content（如果请求中没有）
                            if (!req.containsKey("lesson_content")) {
                                Object content = sectionInfo.get("content");
                                if (content == null) content = sectionInfo.get("sectionContent");
                                if (content == null) content = sectionInfo.get("outline");
                                if (content != null && !content.toString().trim().isEmpty()) {
                                    req.put("lesson_content", content);
                                    logger.info("Added lesson_content from section: {}", content);
                                } else {
                                    // 使用章节标题作为默认内容
                                    Object title = sectionInfo.get("title");
                                    if (title == null) title = sectionInfo.get("sectionTitle");
                                    if (title == null) title = sectionInfo.get("name");
                                    if (title != null) {
                                        req.put("lesson_content", title.toString());
                                        logger.info("Added lesson_content from section title: {}", title);
                                    } else {
                                        req.put("lesson_content", "第" + sectionId + "章节内容");
                                        logger.info("Added default lesson_content for section: {}", sectionId);
                                    }
                                }
                            }
                            
                            // 添加章节标题
                            if (!req.containsKey("section_title")) {
                                Object sectionTitle = sectionInfo.get("title");
                                if (sectionTitle == null) sectionTitle = sectionInfo.get("sectionTitle");
                                if (sectionTitle == null) sectionTitle = sectionInfo.get("name");
                                if (sectionTitle != null) {
                                    req.put("section_title", sectionTitle);
                                    logger.info("Added section_title: {}", sectionTitle);
                                }
                            }
                            
                            // 如果章节信息包含课程ID，也获取课程信息
                            if (!req.containsKey("course_id")) {
                                Object courseIdFromSection = sectionInfo.get("courseId");
                                if (courseIdFromSection != null) {
                                    req.put("course_id", courseIdFromSection);
                                    logger.info("Found course_id from section: {}", courseIdFromSection);
                                    // 递归调用以获取课程信息（避免无限递归）
                                    if (!req.containsKey("_enriched_course")) {
                                        req.put("_enriched_course", true);
                                        enrichWithCourseData(req);
                                        req.remove("_enriched_course");
                                    }
                                }
                            }
                        } else {
                            logger.warn("Section service returned null or no data for section_id: {}, response: {}", sectionId, result);
                        }
                    } catch (Exception e) {
                        logger.error("Failed to fetch section info for section_id {}: {}", sectionId, e.getMessage());
                        // 不抛出异常，继续处理其他信息
                    }
                }
            }
            
            // 获取班级信息
            if (req.containsKey("class_id")) {
                Object classIdObj = req.get("class_id");
                Long classId = null;
                if (classIdObj instanceof Number) {
                    classId = ((Number) classIdObj).longValue();
                } else if (classIdObj instanceof String) {
                    try {
                        classId = Long.parseLong((String) classIdObj);
                    } catch (NumberFormatException e) {
                        logger.warn("Invalid class_id format: {}", classIdObj);
                        return;
                    }
                }
                
                if (classId != null) {
                    try {
                        logger.debug("Fetching class info for class_id: {}", classId);
                        Map<String, Object> classInfo = courseClient.getClassById(classId);
                        if (classInfo != null) {
                            logger.debug("Successfully fetched class info: {}", classInfo.keySet());
                            // 添加班级名称
                            Object className = classInfo.get("className");
                            if (className == null) className = classInfo.get("name");
                            if (className != null) {
                                req.put("class_name", className);
                                logger.debug("Added class_name: {}", className);
                            }
                            // 如果班级信息包含课程ID，也获取课程信息
                            if (!req.containsKey("course_id")) {
                                Object courseIdFromClass = classInfo.get("courseId");
                                if (courseIdFromClass != null) {
                                    req.put("course_id", courseIdFromClass);
                                    logger.debug("Found course_id from class: {}", courseIdFromClass);
                                    // 递归调用以获取课程信息（避免无限递归）
                                    if (!req.containsKey("_enriched_course")) {
                                        req.put("_enriched_course", true);
                                        enrichWithCourseData(req);
                                        req.remove("_enriched_course");
                                    }
                                }
                            }
                        } else {
                            logger.warn("Class info is null for class_id: {}", classId);
                        }
                    } catch (Exception e) {
                        logger.error("Failed to fetch class info for class_id {}: {}", classId, e.getMessage());
                        // 不抛出异常，继续处理其他信息
                    }
                }
            }
            
        } catch (Exception e) {
            // 最外层异常处理：跨服务调用失败不应该阻断AI服务调用，只记录日志
            logger.error("Unexpected error in enrichWithCourseData: {}", e.getMessage(), e);
        }
    }

}
