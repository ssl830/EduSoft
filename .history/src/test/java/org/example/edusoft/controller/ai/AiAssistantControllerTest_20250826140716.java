package org.example.edusoft.controller.ai;

import org.example.edusoft.service.ai.AiAssistantService;
import org.example.edusoft.service.selfpractice.SelfPracticeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AiAssistantController.class)
class AiAssistantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AiAssistantService aiAssistantService;

    @MockBean
    private SelfPracticeService selfPracticeService;

    // 1. uploadEmbeddingFile
    @Test
    void uploadEmbeddingFile_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "abc".getBytes());
        Mockito.when(aiAssistantService.uploadEmbeddingFile(any(), anyString()))
                .thenReturn(Map.of("status", "ok"));
        mockMvc.perform(multipart("/api/ai/embedding/upload").file(file).param("course_id", "c1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }
    @Test
    void uploadEmbeddingFile_fail() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "abc".getBytes());
        Mockito.when(aiAssistantService.uploadEmbeddingFile(any(), anyString()))
                .thenThrow(new RuntimeException("fail"));
        mockMvc.perform(multipart("/api/ai/embedding/upload").file(file).param("course_id", "c1"))
                .andExpect(status().is5xxServerError());
    }

    // 2. generateTeachingContent
    @Test
    void generateTeachingContent_success() throws Exception {
        Mockito.when(aiAssistantService.generateTeachingContent(anyMap()))
                .thenReturn(Map.of("content", "abc"));
        mockMvc.perform(post("/api/ai/rag/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"key\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("abc"));
    }
    @Test
    void generateTeachingContent_fail() throws Exception {
        Mockito.when(aiAssistantService.generateTeachingContent(anyMap()))
                .thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/api/ai/rag/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"key\":1}"))
                .andExpect(status().is5xxServerError());
    }

    // 3. generateTeachingContentDetail
    @Test
    void generateTeachingContentDetail_success() throws Exception {
        Mockito.when(aiAssistantService.generateTeachingContentDetail(anyMap()))
                .thenReturn(Map.of("detail", "abc"));
        mockMvc.perform(post("/api/ai/rag/detail")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"key\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("abc"));
    }
    @Test
    void generateTeachingContentDetail_fail() throws Exception {
        Mockito.when(aiAssistantService.generateTeachingContentDetail(anyMap()))
                .thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/api/ai/rag/detail")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"key\":1}"))
                .andExpect(status().is5xxServerError());
    }

    // 4. regenerateTeachingContent
    @Test
    void regenerateTeachingContent_success() throws Exception {
        Mockito.when(aiAssistantService.regenerateTeachingContent(anyMap()))
                .thenReturn(Map.of("regen", "ok"));
        mockMvc.perform(post("/api/ai/rag/regenerate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"key\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.regen").value("ok"));
    }
    @Test
    void regenerateTeachingContent_fail() throws Exception {
        Mockito.when(aiAssistantService.regenerateTeachingContent(anyMap()))
                .thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/api/ai/rag/regenerate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"key\":1}"))
                .andExpect(status().is5xxServerError());
    }

    // 5. generateExercises
    @Test
    void generateExercises_success() throws Exception {
        Mockito.when(aiAssistantService.generateExercises(anyMap()))
                .thenReturn(Map.of("exercise", "ok"));
        mockMvc.perform(post("/api/ai/rag/generate_exercise")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"key\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exercise").value("ok"));
    }
    @Test
    void generateExercises_fail() throws Exception {
        Mockito.when(aiAssistantService.generateExercises(anyMap()))
                .thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/api/ai/rag/generate_exercise")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"key\":1}"))
                .andExpect(status().is5xxServerError());
    }

    // 6. onlineAssistant
    @Test
    void onlineAssistant_success() throws Exception {
        Mockito.when(aiAssistantService.onlineAssistantWithMemory(anyMap()))
                .thenReturn(Map.of("assistant", "ok"));
        mockMvc.perform(post("/api/ai/rag/assistant")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"msg\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assistant").value("ok"));
    }
    @Test
    void onlineAssistant_fail() throws Exception {
        Mockito.when(aiAssistantService.onlineAssistantWithMemory(anyMap()))
                .thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/api/ai/rag/assistant")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"msg\":1}"))
                .andExpect(status().is5xxServerError());
    }

    // 7. evaluateSubjective
    @Test
    void evaluateSubjective_success() throws Exception {
        Mockito.when(aiAssistantService.evaluateSubjective(anyMap()))
                .thenReturn(Map.of("eva", "ok"));
        mockMvc.perform(post("/api/ai/evaluate-subjective")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"msg\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eva").value("ok"));
    }
    @Test
    void evaluateSubjective_fail() throws Exception {
        Mockito.when(aiAssistantService.evaluateSubjective(anyMap()))
                .thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/api/ai/evaluate-subjective")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"msg\":1}"))
                .andExpect(status().is5xxServerError());
    }

    // 8. generateStudentExercise（只测service抛异常的情况，更多复杂校验建议补充）
    @Test
    void generateStudentExercise_fail() throws Exception {
        Mockito.when(aiAssistantService.generateStudentExercise(anyMap()))
                .thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/api/ai/rag/generate_student_exercise")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"msg\":1}"))
                .andExpect(status().is5xxServerError());
    }

    // 9. reviseTeachingContent
    @Test
    void reviseTeachingContent_success() throws Exception {
        Mockito.when(aiAssistantService.reviseTeachingContent(anyMap()))
                .thenReturn(Map.of("feedback", "ok"));
        mockMvc.perform(post("/api/ai/rag/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"msg\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feedback").value("ok"));
    }
    @Test
    void reviseTeachingContent_fail() throws Exception {
        Mockito.when(aiAssistantService.reviseTeachingContent(anyMap()))
                .thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/api/ai/rag/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"msg\":1}"))
                .andExpect(status().is5xxServerError());
    }

    // 10. generateStepDetail
    @Test
    void generateStepDetail_success() throws Exception {
        Mockito.when(aiAssistantService.generateStepDetail(anyMap()))
                .thenReturn(Map.of("step", "ok"));
        mockMvc.perform(post("/api/ai/rag/step_detail")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"msg\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.step").value("ok"));
    }
    @Test
    void generateStepDetail_fail() throws Exception {
        Mockito.when(aiAssistantService.generateStepDetail(anyMap()))
                .thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/api/ai/rag/step_detail")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"msg\":1}"))
                .andExpect(status().is5xxServerError());
    }

    // 11. analyzeExercise
    @Test
    void analyzeExercise_success() throws Exception {
        Mockito.when(aiAssistantService.analyzeExercise(anyLong())).thenReturn(Map.of("analyze", "ok"));
        mockMvc.perform(post("/api/ai/analyze-exercise")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"practiceId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.analyze").value("ok"));
    }
    @Test
    void analyzeExercise_fail() throws Exception {
        Mockito.when(aiAssistantService.analyzeExercise(anyLong())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/api/ai/analyze-exercise")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"practiceId\":1}"))
                .andExpect(status().is5xxServerError());
    }
    @Test
    void analyzeExercise_missingPracticeId() throws Exception {
        mockMvc.perform(post("/api/ai/analyze-exercise")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"other\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("fail"));
    }

    // 12. generateSectionTeachingContent
    @Test
    void generateSectionTeachingContent_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "sec.txt", "text/plain", "data".getBytes());
        Mockito.when(aiAssistantService.generateSectionTeachingContent(any(), anyString(), anyString(), anyInt(), any()))
                .thenReturn(Map.of("section", "ok"));
        mockMvc.perform(multipart("/api/ai/rag/generate_section")
                .file(file)
                .param("course_name", "c")
                .param("section_title", "s")
                .param("expected_hours", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.section").value("ok"));
    }
    @Test
    void generateSectionTeachingContent_fail() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "sec.txt", "text/plain", "data".getBytes());
        Mockito.when(aiAssistantService.generateSectionTeachingContent(any(), anyString(), anyString(), anyInt(), any()))
                .thenThrow(new RuntimeException("fail"));
        mockMvc.perform(multipart("/api/ai/rag/generate_section")
                .file(file)
                .param("course_name", "c")
                .param("section_title", "s")
                .param("expected_hours", "1"))
                .andExpect(status().is5xxServerError());
    }

    // 13. generateSelectedStudentExercise
    @Test
    void generateSelectedStudentExercise_fail() throws Exception {
        Mockito.when(aiAssistantService.generateSelectedStudentExercise(anyMap()))
                .thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/api/ai/rag/generate_selected_student_exercise")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"msg\":1}"))
                .andExpect(status().is5xxServerError());
    }

    // 14. setBasePath
    @Test
    void setBasePath_success() throws Exception {
        Mockito.when(aiAssistantService.setBasePath(anyString()))
                .thenReturn(Map.of("base", "ok"));
        mockMvc.perform(post("/api/ai/embedding/base_path")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"base_path\":\"path\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.base").value("ok"));
    }
    @Test
    void setBasePath_fail() throws Exception {
        Mockito.when(aiAssistantService.setBasePath(anyString()))
                .thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/api/ai/embedding/base_path")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"base_path\":\"path\"}"))
                .andExpect(status().is5xxServerError());
    }

    // 15. resetBasePath
    @Test
    void resetBasePath_success() throws Exception {
        Mockito.when(aiAssistantService.resetBasePath()).thenReturn(Map.of("reset", "ok"));
        mockMvc.perform(post("/api/ai/embedding/base_path/reset"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reset").value("ok"));
    }
    @Test
    void resetBasePath_fail() throws Exception {
        Mockito.when(aiAssistantService.resetBasePath()).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/api/ai/embedding/base_path/reset"))
                .andExpect(status().is5xxServerError());
    }

    // 16. listKnowledgeBases
    @Test
    void listKnowledgeBases_success() throws Exception {
        Mockito.when(aiAssistantService.listKnowledgeBases()).thenReturn(List.of("a", "b"));
        mockMvc.perform(get("/api/ai/storage/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("a"));
    }
    @Test
    void listKnowledgeBases_fail() throws Exception {
        Mockito.when(aiAssistantService.listKnowledgeBases()).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(get("/api/ai/storage/list"))
                .andExpect(status().is5xxServerError());
    }

    // 17. setSelectedKnowledgeBases
    @Test
    void setSelectedKnowledgeBases_success() throws Exception {
        Mockito.when(aiAssistantService.setSelectedKBs(anyList())).thenReturn(Map.of("selected", "ok"));
        mockMvc.perform(post("/api/ai/storage/selected")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[\"a\",\"b\"]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.selected").value("ok"));
    }
    @Test
    void setSelectedKnowledgeBases_fail() throws Exception {
        Mockito.when(aiAssistantService.setSelectedKBs(anyList())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/api/ai/storage/selected")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[\"a\",\"b\"]"))
                .andExpect(status().is5xxServerError());
    }

    // 18. checkDocumentExists
    @Test
    void checkDocumentExists_success() throws Exception {
        Mockito.when(aiAssistantService.documentExists(anyString(), any())).thenReturn(Map.of("exists", true));
        mockMvc.perform(get("/api/ai/storage/document_exists?filename=file.txt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true));
    }
    @Test
    void checkDocumentExists_fail() throws Exception {
        Mockito.when(aiAssistantService.documentExists(anyString(), any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(get("/api/ai/storage/document_exists?filename=file.txt"))
                .andExpect(status().is5xxServerError());
    }
}