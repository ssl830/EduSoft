package org.example.edusoft.service.chat;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.edusoft.entity.chat.ChatMessage;
import org.example.edusoft.entity.chat.ChatMemorySummary;
import org.example.edusoft.entity.chat.ChatSession;
import org.example.edusoft.mapper.chat.ChatMessageMapper;
import org.example.edusoft.mapper.chat.ChatMemorySummaryMapper;
import org.example.edusoft.mapper.chat.ChatSessionMapper;
import org.example.edusoft.service.ai.AiServiceCaller;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatManagementServiceTest {

    @Mock
    private ChatSessionMapper chatSessionMapper;
    @Mock
    private ChatMessageMapper chatMessageMapper;
    @Mock
    private ChatMemorySummaryMapper chatMemorySummaryMapper;
    @Mock
    private AiServiceCaller aiServiceCaller;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private org.example.edusoft.service.chat.ChatManagementService chatManagementService;

    private static MockedStatic<StpUtil> stpUtilMockedStatic;

    @BeforeAll
    static void initStaticMock() {
        stpUtilMockedStatic = Mockito.mockStatic(StpUtil.class);
        stpUtilMockedStatic.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
    }

    @AfterAll
    static void closeStaticMock() {
        if (stpUtilMockedStatic != null) {
            stpUtilMockedStatic.close();
        }
    }

    // getUserChatSessions
    @Test
    @DisplayName("获取用户会话列表 - 正常返回")
    void testGetUserChatSessions_success() {
        ChatSession session = new ChatSession();
        session.setId(1L);
        session.setSessionTitle("title");
        session.setCourseName("course");
        session.setUpdatedAt(new Date().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime());
        when(chatSessionMapper.getUserActiveSessions(anyLong())).thenReturn(List.of(session));
        ChatMessage msg = new ChatMessage();
        msg.setContent("hello world");
        msg.setRole("user");
        when(chatMessageMapper.getRecentMessages(eq(1L), anyInt())).thenReturn(List.of(msg));

        List<Map<String, Object>> result = chatManagementService.getUserChatSessions();
        assertEquals(1, result.size());
        assertEquals("title", result.get(0).get("title"));
    }

    @Test
    @DisplayName("获取用户会话列表 - 无会话")
    void testGetUserChatSessions_empty() {
        when(chatSessionMapper.getUserActiveSessions(anyLong())).thenReturn(Collections.emptyList());
        List<Map<String, Object>> result = chatManagementService.getUserChatSessions();
        assertTrue(result.isEmpty());
    }

    // createNewChatSession
    @Test
    @DisplayName("创建新会话 - 正常创建")
    void testCreateNewChatSession_success() {
        ChatSession session = new ChatSession();
        session.setId(100L);
        when(chatSessionMapper.insert(any(ChatSession.class))).thenAnswer(invocation -> {
            ChatSession s = invocation.getArgument(0);
            s.setId(100L);
            return 1;
        });
        Long id = chatManagementService.createNewChatSession("course", 1L);
        assertNotNull(id);
    }

    @Test
    @DisplayName("创建新会话 - 插入失败")
    void testCreateNewChatSession_fail() {
        when(chatSessionMapper.insert(any(ChatSession.class))).thenThrow(new RuntimeException("db error"));
        assertThrows(RuntimeException.class, () -> chatManagementService.createNewChatSession("course", 1L));
    }

    // saveChatMessage
    @Test
    @DisplayName("保存消息 - 正常保存")
    void testSaveChatMessage_success() {
        when(chatMessageMapper.getMaxMessageOrder(anyLong())).thenReturn(1);
        when(chatMessageMapper.insert(any(ChatMessage.class))).thenReturn(1);
        doNothing().when(chatSessionMapper).updateSessionTime(anyLong());
        chatManagementService.saveChatMessage(1L, "user", "content", null, null);
        verify(chatMessageMapper, times(1)).insert(any(ChatMessage.class));
    }

    @Test
    @DisplayName("保存消息 - 异常抛出")
    void testSaveChatMessage_exception() {
        when(chatMessageMapper.getMaxMessageOrder(anyLong())).thenThrow(new RuntimeException("db error"));
        assertThrows(RuntimeException.class, () -> chatManagementService.saveChatMessage(1L, "user", "content", null, null));
    }

    // getSessionMemoryContext
    @Test
    @DisplayName("获取记忆上下文 - 正常返回")
    void testGetSessionMemoryContext_success() {
        ChatMemorySummary summary = new ChatMemorySummary();
        summary.setSummaryContent("sum");
        summary.setTokenCount(10);
        when(chatMemorySummaryMapper.getSessionSummaries(anyLong())).thenReturn(List.of(summary));
        ChatMessage msg = new ChatMessage();
        msg.setRole("user");
        msg.setContent("msg");
        msg.setTokenCount(5);
        when(chatMessageMapper.getRecentMessages(anyLong(), anyInt())).thenReturn(List.of(msg));
        List<Map<String, String>> result = chatManagementService.getSessionMemoryContext(1L);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("获取记忆上下文 - 异常返回空")
    void testGetSessionMemoryContext_exception() {
        when(chatMemorySummaryMapper.getSessionSummaries(anyLong())).thenThrow(new RuntimeException("err"));
        List<Map<String, String>> result = chatManagementService.getSessionMemoryContext(1L);
        assertTrue(result.isEmpty());
    }

    // deleteChatSession
    @Test
    @DisplayName("删除会话 - 正常删除")
    void testDeleteChatSession_success() {
        ChatSession session = new ChatSession();
        session.setId(1L);
        session.setUserId(1L); // 设置为当前用户ID
        when(chatSessionMapper.selectById(1L)).thenReturn(session);
        when(chatSessionMapper.updateById(any(ChatSession.class))).thenReturn(1);

        chatManagementService.deleteChatSession(1L);
        verify(chatSessionMapper, times(1)).updateById(any(ChatSession.class));
    }

    @Test
    @DisplayName("删除会话 - 非本人或不存在")
    void testDeleteChatSession_fail() {
        // 测试会话不存在的情况
        when(chatSessionMapper.selectById(1L)).thenReturn(null);
        assertThrows(RuntimeException.class, () -> chatManagementService.deleteChatSession(1L));

        // 测试会话不属于当前用户的情况
        ChatSession session = new ChatSession();
        session.setId(1L);
        session.setUserId(2L); // 设置为其他用户ID
        when(chatSessionMapper.selectById(1L)).thenReturn(session);
        assertThrows(RuntimeException.class, () -> chatManagementService.deleteChatSession(1L));
    }

    // getSessionDetail
    @Test
    @DisplayName("获取会话详情 - 正常返回")
    void testGetSessionDetail_success() {
        ChatSession session = new ChatSession();
        session.setId(1L);
        session.setUserId(1L); // 设置为当前用户ID
        session.setIsActive(true);
        when(chatSessionMapper.selectById(1L)).thenReturn(session);
        ChatMessage msg = new ChatMessage();
        msg.setId(10L);
        msg.setRole("user");
        msg.setContent("hello");
        msg.setCreatedAt(new Date().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime());
        when(chatMessageMapper.getSessionMessages(1L)).thenReturn(List.of(msg));
        Map<String, Object> result = chatManagementService.getSessionDetail(1L);
        assertNotNull(result.get("session"));
        assertFalse(((List<?>) result.get("messages")).isEmpty());
    }

    @Test
    @DisplayName("获取会话详情 - 非本人或未激活")
    void testGetSessionDetail_fail() {
        // 测试会话不存在的情况
        when(chatSessionMapper.selectById(1L)).thenReturn(null);
        assertThrows(RuntimeException.class, () -> chatManagementService.getSessionDetail(1L));

        // 测试会话不属于当前用户的情况
        ChatSession session = new ChatSession();
        session.setId(1L);
        session.setUserId(2L); // 设置为其他用户ID
        session.setIsActive(true);
        when(chatSessionMapper.selectById(1L)).thenReturn(session);
        assertThrows(RuntimeException.class, () -> chatManagementService.getSessionDetail(1L));

        // 测试会话未激活的情况
        session.setUserId(1L); // 设置为当前用户ID
        session.setIsActive(false);
        when(chatSessionMapper.selectById(1L)).thenReturn(session);
        assertThrows(RuntimeException.class, () -> chatManagementService.getSessionDetail(1L));
    }
}