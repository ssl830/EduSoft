package org.example.edusoft.controller.ai;

import org.example.edusoft.service.ai.AiAssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.web.context.ContextLoader;
import org.example.edusoft.service.selfpractice.SelfPracticeService;
import java.util.HashMap;

@RestController
@RequestMapping("/api/ai")
public class AiAssistantController {
    private static final Logger logger = LoggerFactory.getLogger(AiAssistantController.class);

    @Autowired
    private AiAssistantService aiAssistantService;

    @Autowired
    private SelfPracticeService selfPracticeService;

    @PostMapping("/embedding/upload")
    public Map<String, Object> uploadEmbeddingFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "course_id", required = false) String courseId
    ) {
        logger.info("收到上传知识库文件请求: file={}, course_id={}", file.getOriginalFilename(), courseId);
        return aiAssistantService.uploadEmbeddingFile(file, courseId);
    }

    @PostMapping("/rag/generate")
    public Map<String, Object> generateTeachingContent(@RequestBody Map<String, Object> req) {
        logger.info("收到生成教案请求: {}", req);
        return aiAssistantService.generateTeachingContent(req);
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

    @PostMapping("/rag/generate_exercise")
    public Map<String, Object> generateExercises(@RequestBody Map<String, Object> req) {
        logger.info("收到生成题目请求: {}", req);
        return aiAssistantService.generateExercises(req);
    }

    @PostMapping("/rag/assistant")
    public Map<String, Object> onlineAssistant(@RequestBody Map<String, Object> req) {
        logger.info("收到在线学习助手请求: {}", req);
        return aiAssistantService.onlineAssistantWithMemory(req);
    }
    
    @PostMapping("/evaluate-subjective")
    public Map<String, Object> evaluateSubjective(@RequestBody Map<String, Object> req) {
        logger.info("收到主观题AI评估请求: {}", req);
        return aiAssistantService.evaluateSubjective(req);
    }
    
    @PostMapping("/rag/generate_student_exercise")
    public Map<String, Object> generateStudentExercise(@RequestBody Map<String, Object> req) {
        logger.info("收到学生自测练习生成请求: {}", req);
        Map<String,Object> body = aiAssistantService.generateStudentExercise(req);
        
        // Check if the response has valid structure
        if (body == null || (!body.containsKey("exercises") && !body.containsKey("data"))) {
            logger.error("AI微服务返回的学生自测练习数据格式无效: {}", body);
            body = new HashMap<>();
            body.put("status", "error");
            body.put("message", "生成练习题失败，返回数据格式无效");
            return body;
        }
        
        if (StpUtil.isLogin()) {
            try {
                Long studentId = StpUtil.getLoginIdAsLong();
                logger.info("尝试保存生成的自测练习 - 学生ID: {}", studentId);
                Long pid = selfPracticeService.saveGeneratedPractice(studentId, body);
                logger.info("成功保存生成的自测练习 - 练习ID: {}, 学生ID: {}", pid, studentId);
                body.put("practiceId", pid);
            } catch (Exception e) {
                logger.error("保存自测练习失败 - 详细错误: {}", e.getMessage(), e);
                // Still return generated questions but with error flag
                body.put("saveError", true);
                body.put("errorMessage", "练习题已生成，但保存失败：" + e.getMessage());
            }
        } else {
            logger.warn("用户未登录，无法保存自测练习");
            body.put("loginRequired", true);
        }
        return body;
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

    /**
     * 练习学情分析：统计每题得分率，调用AI微服务分析
     * 请求体需包含 practiceId
     */
    @PostMapping("/analyze-exercise")
    public Map<String, Object> analyzeExercise(@RequestBody Map<String, Object> req) {
        logger.info("收到学情分析请求: {}", req);
        Long practiceId = req.get("practiceId") instanceof Number ? ((Number) req.get("practiceId")).longValue() : null;
        if (practiceId == null) {
            return Map.of("status", "fail", "message", "缺少practiceId");
        }
        // 统计并写入得分率，组装题目内容
        Map<String, Object> result = aiAssistantService.analyzeExercise(practiceId);
        return result;
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

    /**
     * 根据学生选择的题目生成自测练习
     */
    @PostMapping("/rag/generate_selected_student_exercise")
    public Map<String, Object> generateSelectedStudentExercise(@RequestBody Map<String, Object> req) {
        logger.info("收到学生选题自测练习生成请求: {}", req);
        Map<String, Object> body = aiAssistantService.generateSelectedStudentExercise(req);
        
        // Check if the response has valid structure
        if (body == null || (!body.containsKey("exercises") && !body.containsKey("data"))) {
            logger.error("AI微服务返回的学生选题自测练习数据格式无效: {}", body);
            body = new HashMap<>();
            body.put("status", "error");
            body.put("message", "生成练习题失败，返回数据格式无效");
            return body;
        }
        
        if (StpUtil.isLogin()) {
            try {
                Long studentId = StpUtil.getLoginIdAsLong();
                logger.info("尝试保存生成的选题自测练习 - 学生ID: {}", studentId);
                Long pid = selfPracticeService.saveGeneratedPractice(studentId, body);
                logger.info("成功保存生成的选题自测练习 - 练习ID: {}, 学生ID: {}", pid, studentId);
                body.put("practiceId", pid);
            } catch (Exception e) {
                logger.error("保存选题自测练习失败 - 详细错误: {}", e.getMessage(), e);
                // Still return generated questions but with error flag
                body.put("saveError", true);
                body.put("errorMessage", "练习题已生成，但保存失败：" + e.getMessage());
            }
        } else {
            logger.warn("用户未登录，无法保存选题自测练习");
            body.put("loginRequired", true);
        }
        return body;
    }

    // -------------------- 私密知识库目录 --------------------
    @PostMapping("/embedding/base_path")
    public Map<String, Object> setBasePath(@RequestBody Map<String, Object> body) {
        String basePath = (String) body.get("base_path");
        return aiAssistantService.setBasePath(basePath);
    }

    @PostMapping("/embedding/base_path/reset")
    public Map<String, Object> resetBasePath() {
        return aiAssistantService.resetBasePath();
    }

    // -------------------- 联合知识库 --------------------
    @GetMapping("/storage/list")
    public List<String> listKnowledgeBases() {
        return aiAssistantService.listKnowledgeBases();
    }

    @PostMapping("/storage/selected")
    public Map<String,Object> setSelectedKnowledgeBases(@RequestBody List<String> paths){
        return aiAssistantService.setSelectedKBs(paths);
    }

    @GetMapping("/storage/document_exists")
    public Map<String,Object> checkDocumentExists(@RequestParam("filename") String filename,
                                                  @RequestParam(value="course_id", required=false) String courseId){
        return aiAssistantService.documentExists(filename, courseId);
    }
}
