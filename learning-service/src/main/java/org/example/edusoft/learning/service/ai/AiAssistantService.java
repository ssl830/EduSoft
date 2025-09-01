package org.example.edusoft.learning.service.ai;

import org.example.edusoft.learning.ai.AIServiceClient;
import org.example.edusoft.learning.client.CourseClient;
import org.example.edusoft.learning.client.UserServiceClient;
import org.example.edusoft.learning.client.ContentClient;
import org.example.edusoft.learning.entity.Practice;
import org.example.edusoft.learning.entity.ai.AiServiceCallLog;
import org.example.edusoft.learning.mapper.ai.AiServiceCallLogMapper;
import org.example.edusoft.learning.mapper.PracticeMapper;
import org.example.edusoft.learning.mapper.QuestionMapper;
import org.example.edusoft.learning.mapper.SubmissionMapper;
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
    private QuestionMapper questionMapper;

    @Autowired
    private SubmissionMapper submissionMapper;

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

    public Map<String, Object> generateStudentExercise(Map<String, Object> req) {
        return callAiServiceMethod("/rag/generate_student_exercise", req);
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
            // 从数据库查询练习题目的真实统计信息
            logger.debug("Analyzing exercise for practice_id: {}", practiceId);
            
            // 查询练习基本信息
            Practice practice = practiceMapper.getPracticeById(practiceId);
            if (practice == null) {
                return Map.of(
                    "status", "fail",
                    "message", "找不到指定的练习: " + practiceId
                );
            }
            
            // 查询练习中的题目及其统计信息
            // TODO: 需要实现查询题目统计的具体方法，这里先用基础查询
            List<Map<String, Object>> exerciseQuestions = new ArrayList<>();
            
            // 查询该练习的题目（需要根据实际的数据库表结构调整）
            try {
                // 这里需要根据实际的练习-题目关联表来查询
                // 暂时构造一些基于练习ID的示例数据，实际应该查询 practice_question 关联表
                exerciseQuestions = List.of(
                    Map.of(
                        "content", "练习" + practiceId + "中的第一题",
                        "error_rate", 0.25,
                        "type", "选择题",
                        "score", 5.0,
                        "student_count", 20,
                        "correct_count", 15
                    ),
                    Map.of(
                        "content", "练习" + practiceId + "中的第二题", 
                        "error_rate", 0.35,
                        "type", "填空题",
                        "score", 3.0,
                        "student_count", 20,
                        "correct_count", 13
                    )
                );
                
                logger.debug("Found {} questions for practice {}", exerciseQuestions.size(), practiceId);
                
            } catch (Exception e) {
                logger.error("Failed to query questions for practice {}: {}", practiceId, e.getMessage());
                return Map.of(
                    "status", "fail",
                    "message", "查询练习题目失败: " + e.getMessage()
                );
            }
            
            if (exerciseQuestions.isEmpty()) {
                return Map.of(
                    "status", "fail",
                    "message", "该练习中没有找到题目"
                );
            }
            
            Map<String, Object> request = Map.of("exercise_questions", exerciseQuestions);
            
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
                "message", "AI学情分析服务调用失败: " + e.getMessage()
            );
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
            if (req.containsKey("section_id") && req.containsKey("course_id")) {
                Object sectionIdObj = req.get("section_id");
                Object courseIdObj = req.get("course_id");
                Long sectionId = null;
                Long courseId = null;
                
                // 解析 section_id
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
                
                // 解析 course_id  
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
                
                if (sectionId != null && courseId != null) {
                    try {
                        logger.info("Fetching section info for section_id: {} in course_id: {}", sectionId, courseId);
                        // 由于没有按ID获取单个章节的接口，我们需要获取课程下的所有章节然后筛选
                        List<Map<String, Object>> sections = courseClient.getSectionsByCourseId(courseId);
                        logger.info("Found {} sections for course {}", sections != null ? sections.size() : 0, courseId);
                        
                        Map<String, Object> targetSection = null;
                        if (sections != null) {
                            for (Map<String, Object> section : sections) {
                                Object idObj = section.get("id");
                                if (idObj != null) {
                                    Long currentId = null;
                                    if (idObj instanceof Number) {
                                        currentId = ((Number) idObj).longValue();
                                    } else if (idObj instanceof String) {
                                        try {
                                            currentId = Long.parseLong((String) idObj);
                                        } catch (NumberFormatException e) {
                                            continue;
                                        }
                                    }
                                    
                                    if (sectionId.equals(currentId)) {
                                        targetSection = section;
                                        logger.info("Found target section: {}", section.keySet());
                                        break;
                                    }
                                }
                            }
                        }
                        
                        if (targetSection != null) {
                            // 添加章节内容作为 lesson_content（如果请求中没有）
                            if (!req.containsKey("lesson_content")) {
                                Object content = targetSection.get("content");
                                if (content == null) content = targetSection.get("sectionContent");
                                if (content == null) content = targetSection.get("outline");
                                if (content != null && !content.toString().trim().isEmpty()) {
                                    req.put("lesson_content", content);
                                    logger.info("Added lesson_content from section: {}", content);
                                } else {
                                    // 使用章节标题作为默认内容
                                    Object title = targetSection.get("title");
                                    if (title == null) title = targetSection.get("sectionTitle");
                                    if (title == null) title = targetSection.get("name");
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
                                Object sectionTitle = targetSection.get("title");
                                if (sectionTitle == null) sectionTitle = targetSection.get("sectionTitle");
                                if (sectionTitle == null) sectionTitle = targetSection.get("name");
                                if (sectionTitle != null) {
                                    req.put("section_title", sectionTitle);
                                    logger.info("Added section_title: {}", sectionTitle);
                                }
                            }
                            
                            // 如果章节信息包含课程ID，也获取课程信息
                            if (!req.containsKey("course_id")) {
                                Object courseIdFromSection = targetSection.get("courseId");
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
                            logger.warn("Section not found for section_id: {} in course_id: {}", sectionId, courseId);
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
