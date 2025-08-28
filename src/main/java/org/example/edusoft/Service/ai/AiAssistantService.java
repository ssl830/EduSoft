package org.example.edusoft.service.ai;

import org.example.edusoft.entity.ai.AiServiceCallLog;
import org.example.edusoft.mapper.ai.AiServiceCallLogMapper;
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

import org.example.edusoft.mapper.practice.PracticeQuestionStatMapper;
import java.util.List;
import java.util.HashMap;
import cn.dev33.satoken.stp.StpUtil;
import org.example.edusoft.service.practice.QuestionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.edusoft.service.chat.ChatManagementService;



@Service
public class AiAssistantService implements AiServiceCaller {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String aiServiceUrl = "http://localhost:8000"; // Python 微服务地址
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PracticeQuestionStatMapper practiceQuestionStatMapper;

    @Autowired
    private QuestionService questionService;
    
    @Autowired
    private ChatManagementService chatManagementService;

    /**
     * 统计练习每题得分率并调用AI微服务分析
     */
    public Map<String, Object> analyzeExercise(Long practiceId) {
        // 1. 查询所有题目统计信息
        List<Map<String, Object>> statList = practiceQuestionStatMapper.getPracticeQuestionStats(practiceId);
        if (statList == null || statList.isEmpty()) {
            return Map.of("status", "fail", "message", "未找到练习题目");
        }
        // 2. 组装参数
        List<Map<String, Object>> exerciseQuestions = new java.util.ArrayList<>();
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
        System.out.println("[AI调试] analyzeExercise 请求体: " + req);

        // 3. 调用微服务分析
        try {
            String url = buildUrl("/rag/analyze_exercise");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(req, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            return Map.of("status", "fail", "message", "AI学情分析服务调用失败: " + e.getMessage());
        }
    }

    @Autowired
    private AiServiceCallLogMapper aiServiceCallLogMapper;

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

    public Map<String, Object> uploadEmbeddingFile(MultipartFile file, String courseId) {
        long startTime = System.currentTimeMillis();
        String endpoint = "/embedding/upload";
        Long userId = null;
        try {
            if (StpUtil.isLogin()) {
                userId = StpUtil.getLoginIdAsLong();
            }
            Path tempFile = Files.createTempFile("upload-", "-" + file.getOriginalFilename());
            file.transferTo(tempFile.toFile());

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
        Long userId = null;
        try {
            if (StpUtil.isLogin()) {
                userId = StpUtil.getLoginIdAsLong();
            }
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

    public Map<String, Object> generateTeachingContentDetail(Map<String, Object> req) {
        long startTime = System.currentTimeMillis();
        String endpoint = "/rag/detail";
        Long userId = null;
        try {
            if (StpUtil.isLogin()) {
                userId = StpUtil.getLoginIdAsLong();
            }
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
                "message", "AI教案细节生成服务调用失败: " + e.getMessage()
            );
        }
    }

    public Map<String, Object> regenerateTeachingContent(Map<String, Object> req) {
        long startTime = System.currentTimeMillis();
        String endpoint = "/rag/regenerate";
        Long userId = null;
        try {
            if (StpUtil.isLogin()) {
                userId = StpUtil.getLoginIdAsLong();
            }
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
                "message", "AI教案重新生成服务调用失败: " + e.getMessage()
            );
        }
    }

    public Map<String, Object> generateExercises(Map<String, Object> req) {
        long startTime = System.currentTimeMillis();
        String endpoint = "/rag/generate_exercise";
        Long userId = null;
        try {
            if (StpUtil.isLogin()) {
                userId = StpUtil.getLoginIdAsLong();
            }
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
        Long userId = null;
        try {
            if (StpUtil.isLogin()) {
                userId = StpUtil.getLoginIdAsLong();
            }
            String url = buildUrl(endpoint);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(req, headers);
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "success", null);
            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = response.getBody();
            return responseBody;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "fail", e.getMessage());
            return Map.of(
                "status", "fail",
                "message", "AI在线助手服务调用失败: " + e.getMessage()
            );
        }
    }

    /**
     * 带记忆功能的在线学习助手
     */
    public Map<String, Object> onlineAssistantWithMemory(Map<String, Object> req) {
        long startTime = System.currentTimeMillis();
        String endpoint = "/rag/assistant";
        Long userId = null;
        Long sessionId = null;
        
        try {
            if (StpUtil.isLogin()) {
                userId = StpUtil.getLoginIdAsLong();
            }
            
            // 获取请求参数
            String question = (String) req.get("question");
            String courseName = (String) req.get("course_name");
            Object sessionIdObj = req.get("sessionId");
            
            if (sessionIdObj instanceof Number) {
                sessionId = ((Number) sessionIdObj).longValue();
            }
            
            // 如果没有指定sessionId，创建新的会话
            if (sessionId == null) {
                Long courseId = req.get("courseId") instanceof Number ? 
                    ((Number) req.get("courseId")).longValue() : null;
                sessionId = chatManagementService.createNewChatSession(courseName, courseId);
            }
            
            // 保存用户问题
            chatManagementService.saveChatMessage(sessionId, "user", question, null, null);
            
            // 获取会话的记忆上下文，而不是使用前端传来的chat_history
            List<Map<String, String>> memoryContext = chatManagementService.getSessionMemoryContext(sessionId);
            
            // 构建发送给AI服务的请求，使用记忆上下文
            Map<String, Object> aiRequest = new HashMap<>();
            aiRequest.put("question", question);
            aiRequest.put("course_name", courseName);
            aiRequest.put("chat_history", memoryContext);
            
            // 调用AI服务
            String url = buildUrl(endpoint);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(aiRequest, headers);
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = response.getBody();
            
            if (responseBody != null) {
                // 保存AI回答
                String answer = (String) responseBody.get("answer");
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> references = (List<Map<String, Object>>) responseBody.get("references");
                @SuppressWarnings("unchecked")
                List<String> knowledgePoints = (List<String>) responseBody.get("knowledgePoints");
                
                chatManagementService.saveChatMessage(sessionId, "assistant", answer, references, knowledgePoints);
                
                // 在响应中包含sessionId
                responseBody.put("sessionId", sessionId);
            }
            
            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "success", null);
            
            return responseBody;
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "fail", e.getMessage());
            return Map.of(
                "status", "fail",
                "message", "AI在线助手服务调用失败: " + e.getMessage(),
                "sessionId", sessionId
            );
        }
    }

    public Map<String, Object> generateStudentExercise(Map<String, Object> req) {
        long startTime = System.currentTimeMillis();
        String endpoint = "/rag/generate_student_exercise";
        Long userId = null;
        try {
            if (StpUtil.isLogin()) {
                userId = StpUtil.getLoginIdAsLong();
            }
            String url = buildUrl(endpoint);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(req, headers);
            ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(url, requestEntity, (Class<Map<String, Object>>)(Class<?>)Map.class);
            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "success", null);
            return response.getBody();
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "fail", e.getMessage());
            return Map.of(
                "status", "fail",
                "message", "AI学生自测练习生成服务调用失败: " + e.getMessage()
            );
        }
    }

    /**
     * 根据学生自主选择的题目生成自测练习（错题/收藏）
     * 调用 Python 微服务 /rag/generate_selected_student_exercise
     */
    public Map<String, Object> generateSelectedStudentExercise(Map<String, Object> req) {
        long startTime = System.currentTimeMillis();
        String endpoint = "/rag/generate_selected_student_exercise";
        Long userId = null;
        try {
            if (StpUtil.isLogin()) {
                userId = StpUtil.getLoginIdAsLong();
            }

            Map<String, Object> payload = new java.util.HashMap<>();

            // 1. 如果前端直接传 selected_questions，则直接使用
            if (req.containsKey("selected_questions")) {
                payload.put("selected_questions", req.get("selected_questions"));
            } else {
                // 否则根据 questionIds 查询题目内容
                List<Integer> idInts = (List<Integer>) req.get("questionIds");
                List<Long> questionIds = new java.util.ArrayList<>();
                if (idInts != null) {
                    for (Integer i : idInts) {
                        questionIds.add(i.longValue());
                    }
                }

                // 将题目实体转换为 Python 微服务需要的 JSON 结构
                List<Map<String, Object>> selectedQuestions = new java.util.ArrayList<>();
                for (Long qid : questionIds) {
                    org.example.edusoft.entity.practice.Question q = questionService.getQuestionDetail(qid);
                    if (q == null) continue;
                    Map<String, Object> qMap = new java.util.HashMap<>();
                    qMap.put("id", qid);
                    qMap.put("type", q.getType().name());
                    qMap.put("question", q.getContent());
                    qMap.put("options", q.getOptionsList());
                    qMap.put("answer", q.getAnswer());
                    qMap.put("explanation", q.getAnalysis());
                    qMap.put("knowledge_points", java.util.Collections.emptyList());
                    selectedQuestions.add(qMap);
                }
                payload.put("selected_questions", selectedQuestions);
            }

            // 2. 其他字段
            if (req.containsKey("requirements")) {
                payload.put("requirements", req.get("requirements"));
            }
            if (req.containsKey("knowledge_preferences")) {
                payload.put("knowledge_preferences", req.get("knowledge_preferences"));
            }

            String url = aiServiceUrl + endpoint;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);
            ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(url, requestEntity, (Class<Map<String, Object>>)(Class<?>)Map.class);
            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "success", null);
            return response.getBody();
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "fail", e.getMessage());
            return Map.of(
                "status", "fail",
                "message", "AI学生选题自测练习生成服务调用失败: " + e.getMessage()
            );
        }
    }

    public Map<String, Object> evaluateSubjectiveAnswer(String question, String studentAnswer, String referenceAnswer, Double maxScore) {
        long startTime = System.currentTimeMillis();
        String endpoint = "/rag/evaluate_subjective";
        Long userId = null;
        try {
            if (StpUtil.isLogin()) {
                userId = StpUtil.getLoginIdAsLong();
            }
            String url = buildUrl(endpoint);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> body = Map.of(
                "question", question,
                "student_answer", studentAnswer,
                "reference_answer", referenceAnswer,
                "max_score", maxScore
            );
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(url, requestEntity, (Class<Map<String, Object>>)(Class<?>)Map.class);
            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "success", null);
            return response.getBody();
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logAiServiceCall(userId, endpoint, duration, "fail", e.getMessage());
            return Map.of(
                "status", "fail",
                "message", "AI主观题评测调用失败: " + e.getMessage()
            );
        }
    }

    // 兼容 Map<String, Object> 参数的主观题评测方法
    public Map<String, Object> evaluateSubjective(Map<String, Object> req) {
        String question = (String) req.getOrDefault("question", "");
        String studentAnswer = (String) req.getOrDefault("student_answer", "");
        String referenceAnswer = (String) req.getOrDefault("reference_answer", "");
        Double maxScore = req.get("max_score") instanceof Number ? ((Number) req.get("max_score")).doubleValue() : 5.0;
        return evaluateSubjectiveAnswer(question, studentAnswer, referenceAnswer, maxScore);
    }

    public Map<String, Object> reviseTeachingContent(Map<String, Object> req) {
        long startTime = System.currentTimeMillis();
        String endpoint = "/rag/feedback";
        Long userId = null;
        try {
            if (StpUtil.isLogin()) {
                userId = StpUtil.getLoginIdAsLong();
            }
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
                "message", "AI教案反馈修改服务调用失败: " + e.getMessage()
            );
        }
    }

    public Map<String, Object> generateStepDetail(Map<String, Object> req) {
        long startTime = System.currentTimeMillis();
        String endpoint = "/rag/step_detail";
        Long userId = null;
        try {
            if (StpUtil.isLogin()) {
                userId = StpUtil.getLoginIdAsLong();
            }
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
                "message", "AI课时环节细节生成服务调用失败: " + e.getMessage()
            );
        }
    }

    public Map<String, Object> generateSectionTeachingContent(MultipartFile file, String courseName, String sectionTitle, Integer expectedHours, String constraints) {
        long startTime = System.currentTimeMillis();
        String endpoint = "/rag/generate_section";
        Long userId = null;
        try {
            if (StpUtil.isLogin()) {
                userId = StpUtil.getLoginIdAsLong();
            }
            // 临时保存
            Path tempFile = Files.createTempFile("section-outline-", "-" + file.getOriginalFilename());
            file.transferTo(tempFile.toFile());

            String url = buildUrl(endpoint);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(tempFile.toFile()));
            body.add("course_name", courseName);
            body.add("section_title", sectionTitle);
            body.add("expected_hours", expectedHours.toString());
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
            return Map.of("status", "fail", "message", "章节教案生成失败: " + e.getMessage());
        }
    }

    /**
     * 切换 AI 微服务工作目录
     */
    public Map<String, Object> setBasePath(String basePath) {
        String endpoint = "/embedding/base_path";
        long start = System.currentTimeMillis();
        try {
            String url = buildUrl(endpoint);
            Map<String, Object> body = Map.of("base_path", basePath);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> resp = restTemplate.postForEntity(url, entity, Map.class);
            logAiServiceCall(null, endpoint, System.currentTimeMillis() - start, "success", null);
            return resp.getBody();
        } catch (Exception e) {
            logAiServiceCall(null, endpoint, System.currentTimeMillis() - start, "fail", e.getMessage());
            return Map.of("code", 500, "message", "AI base path 设置失败: " + e.getMessage());
        }
    }

    /** 恢复默认工作目录 */
    public Map<String, Object> resetBasePath() {
        String endpoint = "/embedding/base_path/reset";
        long start = System.currentTimeMillis();
        try {
            String url = buildUrl(endpoint);
            ResponseEntity<Map> resp = restTemplate.postForEntity(url, null, Map.class);
            logAiServiceCall(null, endpoint, System.currentTimeMillis() - start, "success", null);
            return resp.getBody();
        } catch (Exception e) {
            logAiServiceCall(null, endpoint, System.currentTimeMillis() - start, "fail", e.getMessage());
            return Map.of("code", 500, "message", "AI base path 重置失败: " + e.getMessage());
        }
    }

    /** 获取当前激活的知识库路径列表 */
    public List<String> listKnowledgeBases() {
        try {
            String url = buildUrl("/storage/list");
            ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody()!=null) {
                return objectMapper.readValue(resp.getBody(), new TypeReference<List<String>>(){});
            }
        } catch (Exception e) {
            // ignore
        }
        return java.util.Collections.emptyList();
    }

    /** 设置联合知识库 */
    public Map<String,Object> setSelectedKBs(List<String> paths) {
        try {
            String url = buildUrl("/storage/selected");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<List<String>> req = new HttpEntity<>(paths, headers);
            ResponseEntity<Map> resp = restTemplate.postForEntity(url, req, Map.class);
            return resp.getBody();
        } catch (Exception e) {
            return Map.of("status","fail","message","AI服务调用失败:"+ e.getMessage());
        }
    }

    /** 检查文件是否已存在于知识库 */
    public Map<String,Object> documentExists(String filename, String courseId){
        try{
            String url = buildUrl("/storage/document_exists?filename=" + java.net.URLEncoder.encode(filename, java.nio.charset.StandardCharsets.UTF_8));
            if(courseId!=null && !courseId.isBlank()){
                url += "&course_id=" + java.net.URLEncoder.encode(courseId, java.nio.charset.StandardCharsets.UTF_8);
            }
            ResponseEntity<Map> resp = restTemplate.getForEntity(url, Map.class);
            return resp.getBody();
        }catch(Exception e){
            return Map.of("status","fail","message","AI服务调用失败:"+e.getMessage());
        }
    }

    /**
     * 直接调用AI服务的通用方法（用于内部服务调用）
     */
    public Map<String, Object> callAiServiceDirectly(String endpoint, Map<String, Object> requestData) {
        try {
            String url = buildUrl(endpoint);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestData, headers);
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                responseBody.put("status", "success");
                responseBody.put("data", responseBody);
            }
            
            return responseBody;
        } catch (Exception e) {
            return Map.of(
                "status", "fail",
                "message", "AI服务调用失败: " + e.getMessage()
            );
        }
    }

    /** 根据当前登录用户角色构建 AI 服务 URL（教师携带 user_id） */
    private String buildUrl(String endpoint) {
        Long uid = null;
        try {
            if (StpUtil.isLogin()) {
                uid = StpUtil.getLoginIdAsLong();
                // 若角色非 teacher 则不附加
                String role = StpUtil.getRoleList().stream().findFirst().orElse("");
                if (!"teacher".equals(role)) {
                    uid = null;
                }
            }
        } catch (Exception ignored) {}
        String suffix = uid != null ? (endpoint.contains("?") ? "&" : "?") + "user_id=" + uid : "";
        return aiServiceUrl + endpoint + suffix;
    }

}

