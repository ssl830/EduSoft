package org.example.edusoft.controller.chat;

import org.example.edusoft.service.chat.ChatManagementService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatManagementService chatManagementService;

    // 1. getChatSessions
    @Test
    void getChatSessions_success() throws Exception {
        List<Map<String, Object>> sessions = Arrays.asList(
            Map.of("sessionId", 1L, "courseName", "Math"),
            Map.of("sessionId", 2L, "courseName", "Physics")
        );
        Mockito.when(chatManagementService.getUserChatSessions()).thenReturn(sessions);

        mockMvc.perform(get("/api/chat/sessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].sessionId").value(1))
                .andExpect(jsonPath("$.data[1].courseName").value("Physics"));
    }

    @Test
    void getChatSessions_fail() throws Exception {
        Mockito.when(chatManagementService.getUserChatSessions())
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/chat/sessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("获取聊天会话失败：Database error"));
    }

    // 2. createChatSession
    @Test
    void createChatSession_success() throws Exception {
        Mockito.when(chatManagementService.createNewChatSession(anyString(), any()))
                .thenReturn(123L);

        mockMvc.perform(post("/api/chat/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"courseName\":\"Math\",\"courseId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.sessionId").value(123));
    }

    @Test
    void createChatSession_fail() throws Exception {
        Mockito.when(chatManagementService.createNewChatSession(anyString(), any()))
                .thenThrow(new RuntimeException("Create failed"));

        mockMvc.perform(post("/api/chat/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"courseName\":\"Math\",\"courseId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("创建聊天会话失败：Create failed"));
    }

    // 3. getSessionDetail
    @Test
    void getSessionDetail_success() throws Exception {
        Map<String, Object> detail = Map.of(
            "sessionId", 1L,
            "messages", Arrays.asList(
                Map.of("id", 1L, "content", "Hello"),
                Map.of("id", 2L, "content", "Hi there")
            )
        );
        Mockito.when(chatManagementService.getSessionDetail(1L)).thenReturn(detail);

        mockMvc.perform(get("/api/chat/sessions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.sessionId").value(1))
                .andExpect(jsonPath("$.data.messages[0].content").value("Hello"));
    }

    @Test
    void getSessionDetail_fail() throws Exception {
        Mockito.when(chatManagementService.getSessionDetail(1L))
                .thenThrow(new RuntimeException("Session not found"));

        mockMvc.perform(get("/api/chat/sessions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("获取会话详情失败：Session not found"));
    }

    // 4. deleteChatSession
    @Test
    void deleteChatSession_success() throws Exception {
        Mockito.doNothing().when(chatManagementService).deleteChatSession(1L);

        mockMvc.perform(delete("/api/chat/sessions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));
    }

    @Test
    void deleteChatSession_fail() throws Exception {
        Mockito.doThrow(new RuntimeException("Delete failed"))
                .when(chatManagementService).deleteChatSession(1L);

        mockMvc.perform(delete("/api/chat/sessions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("删除会话失败：Delete failed"));
    }
}