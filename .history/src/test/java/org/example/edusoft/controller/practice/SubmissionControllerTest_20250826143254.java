package org.example.edusoft.controller.practice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.edusoft.entity.practice.Question;
import org.example.edusoft.entity.practice.QuestionListDTO;
import org.example.edusoft.service.practice.QuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class QuestionControllerTest {

    @Mock
    private QuestionService questionService;

    @InjectMocks
    private QuestionController questionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(questionController).build();
        objectMapper = new ObjectMapper();
    }

    // ===================== createQuestion 测试 =====================

    @Test
    void createQuestion_Success() throws Exception {
        // 准备测试数据
        Question inputQuestion = new Question();
        inputQuestion.setTitle("测试题目");
        inputQuestion.setContent("这是一个测试题目");

        Question createdQuestion = new Question();
        createdQuestion.setId(1L);
        createdQuestion.setTitle("测试题目");
        createdQuestion.setContent("这是一个测试题目");

        when(questionService.createQuestion(any(Question.class))).thenReturn(createdQuestion);

        // 执行测试
        mockMvc.perform(post("/api/practice/question/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputQuestion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("题目创建成功"))
                .andExpect(jsonPath("$.data.questionId").value(1));

        verify(questionService, times(1)).createQuestion(any(Question.class));
    }

    @Test
    void createQuestion_ServiceThrowsException() throws Exception {
        // 准备测试数据
        Question inputQuestion = new Question();
        inputQuestion.setTitle("测试题目");

        when(questionService.createQuestion(any(Question.class)))
                .thenThrow(new RuntimeException("创建题目失败"));

        // 执行测试
        mockMvc.perform(post("/api/practice/question/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputQuestion)))
                .andExpect(status().is5xxServerError());

        verify(questionService, times(1)).createQuestion(any(Question.class));
    }

    // ===================== updateQuestion 测试 =====================

    @Test
    void updateQuestion_Success() throws Exception {
        // 准备测试数据
        Long questionId = 1L;
        Question inputQuestion = new Question();
        inputQuestion.setTitle("更新的题目");

        Question updatedQuestion = new Question();
        updatedQuestion.setId(questionId);
        updatedQuestion.setTitle("更新的题目");

        when(questionService.updateQuestion(any(Question.class))).thenReturn(updatedQuestion);

        // 执行测试
        mockMvc.perform(put("/api/practice/question/{id}", questionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputQuestion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("题目更新成功"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("更新的题目"));

        verify(questionService, times(1)).updateQuestion(any(Question.class));
    }

    @Test
    void updateQuestion_QuestionNotFound() throws Exception {
        // 准备测试数据
        Long questionId = 999L;
        Question inputQuestion = new Question();
        inputQuestion.setTitle("更新的题目");

        when(questionService.updateQuestion(any(Question.class)))
                .thenThrow(new RuntimeException("题目不存在"));

        // 执行测试
        mockMvc.perform(put("/api/practice/question/{id}", questionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputQuestion)))
                .andExpect(status().is5xxServerError());

        verify(questionService, times(1)).updateQuestion(any(Question.class));
    }

    // ===================== getQuestionList 测试 =====================

    @Test
    void getQuestionList_WithCourseId_Success() throws Exception {
        // 准备测试数据
        Long courseId = 1L;
        List<QuestionListDTO> mockQuestions = Arrays.asList(
                createMockQuestionListDTO(1L, "题目1"),
                createMockQuestionListDTO(2L, "题目2")
        );

        when(questionService.getQuestionListByCourse(courseId)).thenReturn(mockQuestions);

        // 执行测试
        mockMvc.perform(get("/api/practice/question/list")
                        .param("courseId", courseId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpected(jsonPath("$.message").value("获取题目列表成功"))
                .andExpect(jsonPath("$.data.questions").isArray())
                .andExpect(jsonPath("$.data.questions.length()").value(2));

        verify(questionService, times(1)).getQuestionListByCourse(courseId);
        verify(questionService, never()).getAllQuestions();
    }

    @Test
    void getQuestionList_WithoutCourseId_Success() throws Exception {
        // 准备测试数据
        List<QuestionListDTO> mockQuestions = Arrays.asList(
                createMockQuestionListDTO(1L, "题目1"),
                createMockQuestionListDTO(2L, "题目2")
        );

        when(questionService.getAllQuestions()).thenReturn(mockQuestions);

        // 执行测试
        mockMvc.perform(get("/api/practice/question/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取题目列表成功"))
                .andExpect(jsonPath("$.data.questions").isArray())
                .andExpect(jsonPath("$.data.questions.length()").value(2));

        verify(questionService, times(1)).getAllQuestions();
        verify(questionService, never()).getQuestionListByCourse(anyLong());
    }

    @Test
    void getQuestionList_ServiceThrowsException() throws Exception {
        when(questionService.getAllQuestions())
                .thenThrow(new RuntimeException("数据库连接失败"));

        mockMvc.perform(get("/api/practice/question/list"))
                .andExpect(status().is5xxServerError());

        verify(questionService, times(1)).getAllQuestions();
    }

    // ===================== getQuestionDetail 测试 =====================

    @Test
    void getQuestionDetail_Success() throws Exception {
        // 准备测试数据
        Long questionId = 1L;
        Question mockQuestion = new Question();
        mockQuestion.setId(questionId);
        mockQuestion.setTitle("详细题目");
        mockQuestion.setContent("这是详细内容");

        when(questionService.getQuestionDetail(questionId)).thenReturn(mockQuestion);

        // 执行测试
        mockMvc.perform(get("/api/practice/question/{id}", questionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取题目详情成功"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("详细题目"));

        verify(questionService, times(1)).getQuestionDetail(questionId);
    }

    @Test
    void getQuestionDetail_QuestionNotFound() throws Exception {
        // 准备测试数据
        Long questionId = 999L;

        when(questionService.getQuestionDetail(questionId))
                .thenThrow(new RuntimeException("题目不存在"));

        // 执行测试
        mockMvc.perform(get("/api/practice/question/{id}", questionId))
                .andExpected(status().is5xxServerError());

        verify(questionService, times(1)).getQuestionDetail(questionId);
    }

    // ===================== deleteQuestion 测试 =====================

    @Test
    void deleteQuestion_Success() throws Exception {
        // 准备测试数据
        Long questionId = 1L;

        doNothing().when(questionService).deleteQuestion(questionId);

        // 执行测试
        mockMvc.perform(delete("/api/practice/question/{id}", questionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("题目删除成功"))
                .andExpect(jsonPath("$.data.questionId").value(1));

        verify(questionService, times(1)).deleteQuestion(questionId);
    }

    @Test
    void deleteQuestion_QuestionNotFound() throws Exception {
        // 准备测试数据
        Long questionId = 999L;

        doThrow(new RuntimeException("题目不存在"))
                .when(questionService).deleteQuestion(questionId);

        // 执行测试
        mockMvc.perform(delete("/api/practice/question/{id}", questionId))
                .andExpect(status().is5xxServerError());

        verify(questionService, times(1)).deleteQuestion(questionId);
    }

    // ===================== importQuestionsToPractice 测试 =====================

    @Test
    void importQuestionsToPractice_Success() throws Exception {
        // 准备测试数据
        Map<String, Object> request = new HashMap<>();
        request.put("practiceId", 1);
        request.put("questionIds", Arrays.asList(1, 2, 3));
        request.put("scores", Arrays.asList(10, 15, 20));

        doNothing().when(questionService).importQuestionsToPractice(
                eq(1L), 
                eq(Arrays.asList(1L, 2L, 3L)), 
                eq(Arrays.asList(10, 15, 20))
        );

        // 执行测试
        mockMvc.perform(post("/api/practice/question/import")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("题目导入成功"))
                .andExpect(jsonPath("$.data.practiceId").value(1))
                .andExpect(jsonPath("$.data.questionCount").value(3));

        verify(questionService, times(1)).importQuestionsToPractice(
                eq(1L), 
                eq(Arrays.asList(1L, 2L, 3L)), 
                eq(Arrays.asList(10, 15, 20))
        );
    }

    @Test
    void importQuestionsToPractice_InvalidData() throws Exception {
        // 准备测试数据 - 缺少必要字段
        Map<String, Object> request = new HashMap<>();
        request.put("practiceId", 1);
        // 故意不添加 questionIds 和 scores

        // 执行测试
        mockMvc.perform(post("/api/practice/question/import")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());

        verify(questionService, never()).importQuestionsToPractice(anyLong(), anyList(), anyList());
    }

    // ===================== getQuestionsBySection 测试 =====================

    @Test
    void getQuestionsBySection_Success() throws Exception {
        // 准备测试数据
        Long courseId = 1L;
        Long sectionId = 2L;
        List<Question> mockQuestions = Arrays.asList(
                createMockQuestion(1L, "章节题目1"),
                createMockQuestion(2L, "章节题目2")
        );

        when(questionService.getQuestionsBySection(courseId, sectionId))
                .thenReturn(mockQuestions);

        // 执行测试
        mockMvc.perform(get("/api/practice/question/section")
                        .param("courseId", courseId.toString())
                        .param("sectionId", sectionId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取章节题目列表成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));

        verify(questionService, times(1)).getQuestionsBySection(courseId, sectionId);
    }

    @Test
    void getQuestionsBySection_MissingParameters() throws Exception {
        // 执行测试 - 缺少必要参数
        mockMvc.perform(get("/api/practice/question/section")
                        .param("courseId", "1"))
                // 故意不添加 sectionId 参数
                .andExpect(status().isBadRequest());

        verify(questionService, never()).getQuestionsBySection(anyLong(), anyLong());
    }

    // ===================== batchCreateQuestions 测试 =====================

    @Test
    void batchCreateQuestions_Success() throws Exception {
        // 准备测试数据
        List<Question> inputQuestions = Arrays.asList(
                createMockQuestion(null, "批量题目1"),
                createMockQuestion(null, "批量题目2")
        );

        List<Question> createdQuestions = Arrays.asList(
                createMockQuestion(1L, "批量题目1"),
                createMockQuestion(2L, "批量题目2")
        );

        when(questionService.batchCreateQuestions(anyList())).thenReturn(createdQuestions);

        // 执行测试
        mockMvc.perform(post("/api/practice/question/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputQuestions)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("批量创建题目成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));

        verify(questionService, times(1)).batchCreateQuestions(anyList());
    }

    @Test
    void batchCreateQuestions_EmptyList() throws Exception {
        // 准备测试数据 - 空列表
        List<Question> emptyQuestions = new ArrayList<>();

        when(questionService.batchCreateQuestions(anyList()))
                .thenThrow(new IllegalArgumentException("题目列表不能为空"));

        // 执行测试
        mockMvc.perform(post("/api/practice/question/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyQuestions)))
                .andExpect(status().is5xxServerError());

        verify(questionService, times(1)).batchCreateQuestions(anyList());
    }

    // ===================== 辅助方法 =====================

    private Question createMockQuestion(Long id, String title) {
        Question question = new Question();
        question.setId(id);
        question.setTitle(title);
        question.setContent("测试内容");
        return question;
    }

    private QuestionListDTO createMockQuestionListDTO(Long id, String title) {
        QuestionListDTO dto = new QuestionListDTO();
        dto.setId(id);
        dto.setTitle(title);
        // 根据实际的 QuestionListDTO 结构设置其他字段
        return dto;
    }
}