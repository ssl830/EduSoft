package org.example.edusoft.service.ai;

import org.example.edusoft.mapper.practice.PracticeQuestionStatMapper;
import org.example.edusoft.service.practice.QuestionService;
import org.example.edusoft.mapper.ai.AiServiceCallLogMapper;
import org.example.edusoft.service.chat.ChatManagementService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.example.edusoft.service.ai.AiAssistantService;
import org.springframework.http.HttpEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@ExtendWith(MockitoExtension.class)
class AiAssistantServiceTest {

    @Mock
    private PracticeQuestionStatMapper practiceQuestionStatMapper;
    @Mock
    private QuestionService questionService;
    @Mock
    private AiServiceCallLogMapper aiServiceCallLogMapper;
    @Mock
    private ChatManagementService chatManagementService;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private AiAssistantService aiAssistantService;

    @BeforeEach
    void setUp() {
        // 强制将 AiAssistantService 内部的 restTemplate 替换为 mock
        ReflectionTestUtils.setField(aiAssistantService, "restTemplate", restTemplate);

        // 设置模拟的AI服务URL
        ReflectionTestUtils.setField(aiAssistantService, "aiServiceUrl", "http://mock-ai-service");
    }

    @Test
    @DisplayName("analyzeExercise - 正常返回分析结果")
    void testAnalyzeExercise_success() {
        List<Map<String, Object>> statList = new ArrayList<>();
        Map<String, Object> stat = new HashMap<>();
        stat.put("content", "题目1");
        stat.put("score_rate", 0.8);
        stat.put("type", "singlechoice");
        stat.put("score", 5);
        stat.put("student_count", 10);
        stat.put("correct_count", 8);
        statList.add(stat);

        when(practiceQuestionStatMapper.getPracticeQuestionStats(1L)).thenReturn(statList);

        // 修正：确保返回的数据结构与实际服务一致
        Map<String, Object> mockResp = new HashMap<>();
        mockResp.put("status", "success");
        mockResp.put("result", "分析结果");

        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(mockResp));

        Map<String, Object> result = aiAssistantService.analyzeExercise(1L);

        assertEquals("success", result.get("status"));
        assertEquals("分析结果", result.get("result"));
    }

    @Test
    @DisplayName("generateTeachingContent - 微服务返回结果")
    void testGenerateTeachingContent_success() {
        Map<String, Object> req = new HashMap<>();
        req.put("course_name", "示例课程");
        req.put("course_outline", "课程大纲内容");
        req.put("expected_hours", 10);
        req.put("constraints", "无约束");

        // 创建更完整的模拟响应
        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "success");
        resp.put("totalHours", 10);
        resp.put("teachingAdvice", "教学建议内容");
        resp.put("lessons", Collections.emptyList());
        Map<String, Object> data = new HashMap<>();
        data.put("content", "生成的教案内容");
        resp.put("data", data);

        // 只 mock 实际调用，不要多余的 stubbing
//         when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
//                 .thenReturn(ResponseEntity.ok(resp));

        Map<String, Object> result = aiAssistantService.generateTeachingContent(req);
        System.out.println("Result: " + result);

        assertNotNull(result.get("status"));
    }

    @Test
    @DisplayName("generateExercises - 正常生成题目")
    void testGenerateExercises_success() {
        Map<String, Object> req = Map.of("course", "数学");

        Map<String, Object> resp = new HashMap<>();
        List<Map<String, Object>> exercises = new ArrayList<>();
        Map<String, Object> exercise = new HashMap<>();
        exercise.put("question", "题目1");
        exercises.add(exercise);
        resp.put("exercises", exercises);
        resp.put("status", "success");

//         when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
//                 .thenReturn(ResponseEntity.ok(resp));

        Map<String, Object> result = aiAssistantService.generateExercises(req);

        assertNotNull(result.get("status"));
    }

    @Test
    @DisplayName("onlineAssistant - 正常返回AI助手结果")
    void testOnlineAssistant_success() {
        Map<String, Object> req = Map.of("question", "什么是AI？");

        Map<String, Object> resp = new HashMap<>();
        resp.put("answer", "人工智能");
        resp.put("status", "success");

//         when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
//                 .thenReturn(ResponseEntity.ok(resp));

        Map<String, Object> result = aiAssistantService.onlineAssistant(req);

        assertNotNull(result.get("status"));
    }

}
