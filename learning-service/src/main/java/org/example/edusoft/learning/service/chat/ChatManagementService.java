package org.example.edusoft.learning.service.chat;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.edusoft.learning.entity.chat.ChatMessage;
import org.example.edusoft.learning.entity.chat.ChatMemorySummary;
import org.example.edusoft.learning.entity.chat.ChatSession;
import org.example.edusoft.learning.mapper.chat.ChatMessageMapper;
import org.example.edusoft.learning.mapper.chat.ChatMemorySummaryMapper;
import org.example.edusoft.learning.mapper.chat.ChatSessionMapper;
import org.example.edusoft.learning.service.ai.AiServiceCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 聊天管理服务
 * 实现类似langchain memory的记忆功能
 */
@Slf4j
@Service
public class ChatManagementService {
    
    @Autowired
    private ChatSessionMapper chatSessionMapper;
    
    @Autowired
    private ChatMessageMapper chatMessageMapper;
    
    @Autowired
    private ChatMemorySummaryMapper chatMemorySummaryMapper;
    
    @Autowired
    @Lazy
    private AiServiceCaller aiServiceCaller;
    
    @Autowired
    private ChatMemoryService chatMemoryService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // Memory配置常量
    private static final int MAX_CONTEXT_TOKENS = 8000;  // 最大上下文token数
    private static final int MAX_RECENT_MESSAGES = 10;   // 最多保留的近期消息数
    private static final int SUMMARY_TRIGGER_MESSAGES = 20; // 触发摘要的消息数量
    
    /**
     * 获取用户的聊天会话列表
     */
    public List<Map<String, Object>> getUserChatSessions() {
        Long userId = getCurrentUserId();
        List<ChatSession> sessions = chatSessionMapper.getUserActiveSessions(userId);
        
        return sessions.stream().map(session -> {
            Map<String, Object> sessionInfo = new HashMap<>();
            sessionInfo.put("id", session.getId());
            sessionInfo.put("title", session.getSessionTitle());
            sessionInfo.put("courseName", session.getCourseName());
            sessionInfo.put("createdAt", session.getCreatedAt());
            sessionInfo.put("updatedAt", session.getUpdatedAt());
            return sessionInfo;
        }).collect(Collectors.toList());
    }
    
    /**
     * 创建新的聊天会话
     */
    @Transactional
    public Long createNewChatSession(String courseName, Long courseId) {
        Long userId = getCurrentUserId();
        
        // 先将用户的所有会话设为非活跃状态
        chatSessionMapper.deactivateAllUserSessions(userId);
        
        // 创建新会话
        ChatSession newSession = new ChatSession()
                .setUserId(userId)
                .setCourseName(courseName)
                .setCourseId(courseId)
                .setSessionTitle("New Chat")
                .setIsActive(true);
        
        chatSessionMapper.insert(newSession);
        log.info("Created new chat session {} for user {}", newSession.getId(), userId);
        
        return newSession.getId();
    }
    
    /**
     * 保存聊天消息
     */
    @Transactional
    public void saveChatMessage(Long sessionId, String role, String content, String references, String knowledgePoints) {
        // 获取下一个消息顺序号
        Integer nextOrder = chatMessageMapper.getMaxMessageOrder(sessionId) + 1;
        
        ChatMessage message = new ChatMessage()
                .setSessionId(sessionId)
                .setRole(role)
                .setContent(content)
                .setReferences(references)
                .setKnowledgePoints(knowledgePoints)
                .setMessageOrder(nextOrder)
                .setTokenCount(estimateTokenCount(content));
        
        chatMessageMapper.insert(message);
        
        // 更新会话时间
        chatSessionMapper.updateSessionTime(sessionId);
        
        // 如果是第一条用户消息，更新会话标题
        if (nextOrder == 1 && "user".equals(role)) {
            String title = content.length() > 20 ? content.substring(0, 20) + "..." : content;
            chatSessionMapper.updateSessionTitle(sessionId, title);
        }
        
        // 检查是否需要生成摘要
        checkAndGenerateSummary(sessionId);
    }
    
    /**
     * 获取会话的记忆上下文
     */
    public List<Map<String, String>> getSessionMemoryContext(Long sessionId) {
        List<Map<String, String>> context = new ArrayList<>();
        
        // 1. 获取摘要信息
        List<ChatMemorySummary> summaries = chatMemorySummaryMapper.getSessionSummaries(sessionId);
        if (!summaries.isEmpty()) {
            for (ChatMemorySummary summary : summaries) {
                context.add(Map.of(
                    "role", "system",
                    "content", "历史对话摘要: " + summary.getSummaryContent()
                ));
            }
        }
        
        // 2. 获取最近的消息
        List<ChatMessage> recentMessages = chatMessageMapper.getRecentMessages(sessionId, MAX_RECENT_MESSAGES);
        Collections.reverse(recentMessages); // 按时间正序排列
        
        for (ChatMessage message : recentMessages) {
            context.add(Map.of(
                "role", message.getRole(),
                "content", message.getContent()
            ));
        }
        
        return context;
    }
    
    /**
     * 获取会话详情和消息历史
     */
    public Map<String, Object> getSessionDetail(Long sessionId) {
        // 获取会话信息
        ChatSession session = chatSessionMapper.selectById(sessionId);
        if (session == null) {
            throw new RuntimeException("会话不存在");
        }
        
        // 获取消息列表
        List<ChatMessage> messages = chatMessageMapper.getSessionMessages(sessionId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("session", session);
        result.put("messages", messages);
        
        return result;
    }
    
    /**
     * 删除聊天会话
     */
    @Transactional
    public void deleteChatSession(Long sessionId) {
        // 因为设置了级联删除，删除会话会自动删除相关的消息和摘要
        chatSessionMapper.deleteById(sessionId);
        log.info("Deleted chat session {}", sessionId);
    }
    
    /**
     * 检查并生成摘要
     */
    private void checkAndGenerateSummary(Long sessionId) {
        Integer messageCount = chatMessageMapper.getMessageCount(sessionId);
        
        // 如果消息数量达到触发阈值，生成摘要
        if (messageCount >= SUMMARY_TRIGGER_MESSAGES) {
            ChatMemorySummary latestSummary = chatMemorySummaryMapper.getLatestSummary(sessionId);
            
            // 确定要摘要的消息范围
            int startOrder = latestSummary != null ? latestSummary.getMessageRangeEnd().intValue() + 1 : 1;
            int endOrder = messageCount - MAX_RECENT_MESSAGES; // 保留最近的消息不被摘要
            
            if (endOrder > startOrder) {
                generateAndSaveSummary(sessionId, startOrder, endOrder);
            }
        }
    }
    
    /**
     * 生成并保存摘要
     */
    private void generateAndSaveSummary(Long sessionId, int startOrder, int endOrder) {
        try {
            // 获取指定范围的消息
            List<ChatMessage> messages = chatMessageMapper.getMessagesByRange(sessionId, startOrder, endOrder);
            
            // 生成摘要
            String summaryContent = chatMemoryService.generateSummary(messages);
            
            // 保存摘要
            ChatMemorySummary summary = new ChatMemorySummary()
                    .setSessionId(sessionId)
                    .setSummaryContent(summaryContent)
                    .setMessageRangeStart((long) startOrder)
                    .setMessageRangeEnd((long) endOrder)
                    .setTokenCount(estimateTokenCount(summaryContent));
            
            chatMemorySummaryMapper.insert(summary);
            
            log.info("Generated summary for session {} covering messages {}-{}", sessionId, startOrder, endOrder);
            
        } catch (Exception e) {
            log.error("Failed to generate summary for session {}: {}", sessionId, e.getMessage());
        }
    }
    
    /**
     * 估算文本的token数量
     */
    private int estimateTokenCount(String text) {
        if (text == null) return 0;
        // 简单估算：中文按字符数，英文按单词数*1.3
        return (int) (text.length() * 1.2);
    }
    
    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        // TODO: 实现获取当前登录用户ID的逻辑
        return 1L; // 临时返回固定值
    }
}
