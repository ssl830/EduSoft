package org.example.edusoft.service.chat;

import org.example.edusoft.entity.chat.ChatMessage;
import org.example.edusoft.service.ai.AiServiceCaller;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatMemoryServiceTest {

    @Mock
    private AiServiceCaller aiServiceCaller;

    @InjectMocks
    private org.example.edusoft.service.chat.ChatMemoryService chatMemoryService;

    @Test
    @DisplayName("生成摘要 - AI服务成功返回")
    void testGenerateSummary_success() {
        List<ChatMessage> messages = Arrays.asList(
                new ChatMessage("user", "什么是Java？"),
                new ChatMessage("assistant", "Java是一种编程语言。")
        );
        Map<String, Object> aiResponse = new HashMap<>();
        aiResponse.put("status", "success");
        Map<String, Object> data = new HashMap<>();
        data.put("answer", "Java相关知识总结");
        aiResponse.put("data", data);

        when(aiServiceCaller.callAiServiceDirectly(anyString(), anyMap())).thenReturn(aiResponse);

        String summary = chatMemoryService.generateSummary(messages);

        assertEquals("Java相关知识总结", summary);
        verify(aiServiceCaller, times(1)).callAiServiceDirectly(anyString(), anyMap());
    }

    @Test
    @DisplayName("生成摘要 - AI服务失败，使用备用摘要")
    void testGenerateSummary_fallback() {
        List<ChatMessage> messages = Arrays.asList(
                new ChatMessage("user", "如何学习Python？"),
                new ChatMessage("assistant", "可以通过多���习。")
        );
        Map<String, Object> aiResponse = new HashMap<>();
        aiResponse.put("status", "fail");

        when(aiServiceCaller.callAiServiceDirectly(anyString(), anyMap())).thenReturn(aiResponse);

        String summary = chatMemoryService.generateSummary(messages);

        assertTrue(summary.contains("讨论了"));
        verify(aiServiceCaller, times(1)).callAiServiceDirectly(anyString(), anyMap());
    }

    @Test
    @DisplayName("生成摘要 - 消息列表为空")
    void testGenerateSummary_emptyMessages() {
        List<ChatMessage> messages = Collections.emptyList();

        String summary = chatMemoryService.generateSummary(messages);

        assertEquals("", summary);
        verify(aiServiceCaller, never()).callAiServiceDirectly(anyString(), anyMap());
    }

    @Test
    @DisplayName("生成摘要 - AI服务异常，使用备用摘要")
    void testGenerateSummary_aiException() {
        List<ChatMessage> messages = Arrays.asList(
                new ChatMessage("user", "异常测试？")
        );
        when(aiServiceCaller.callAiServiceDirectly(anyString(), anyMap())).thenThrow(new RuntimeException("AI异常"));

        String summary = chatMemoryService.generateSummary(messages);

        assertTrue(summary.contains("讨论了"));
        verify(aiServiceCaller, times(1)).callAiServiceDirectly(anyString(), anyMap());
    }

    @Test
    @DisplayName("生成备用摘要 - 有用户问题")
    void testGenerateFallbackSummary_withUserQuestion() {
        List<ChatMessage> messages = Arrays.asList(
                new ChatMessage("user", "请介绍一下C语言。"),
                new ChatMessage("assistant", "C语言是一种结构化语言。")
        );
        String summary = chatMemoryService.generateFallbackSummary(messages);

        assertTrue(summary.contains("讨论了"));
        assertTrue(summary.contains("请介绍一下C语言"));
    }

    @Test
    @DisplayName("生成备用摘要 - 无用户问题")
    void testGenerateFallbackSummary_noUserQuestion() {
        List<ChatMessage> messages = Arrays.asList(
                new ChatMessage("assistant", "欢迎使用AI助教。")
        );
        String summary = chatMemoryService.generateFallbackSummary(messages);

        assertTrue(summary.contains("学习相关问题"));
    }

    @Test
    @DisplayName("生成备用摘要 - 空消息列表")
    void testGenerateFallbackSummary_empty() {
        List<ChatMessage> messages = Collections.emptyList();
        String summary = chatMemoryService.generateFallbackSummary(messages);

        assertEquals("空对话", summary);
    }
}
