package org.example.edusoft.service.chat;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.edusoft.entity.chat.ChatMessage;
import org.example.edusoft.entity.chat.ChatMemorySummary;
import org.example.edusoft.entity.chat.ChatSession;
import org.example.edusoft.mapper.chat.ChatMessageMapper;
import org.example.edusoft.mapper.chat.ChatMemorySummaryMapper;
import org.example.edusoft.mapper.chat.ChatSessionMapper;
import org.example.edusoft.service.ai.AiServiceCaller;
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
    private ObjectMapper objectMapper;
    
    // Memory配置常量
    private static final int MAX_CONTEXT_TOKENS = 8000;  // 最大上下文token数
    private static final int MAX_RECENT_MESSAGES = 10;   // 最多保留的近期消息数
    private static final int SUMMARY_TRIGGER_MESSAGES = 20; // 触发摘要的消息数量
    
    /**
     * 获取用户的聊天会话列表
     */
    public List<Map<String, Object>> getUserChatSessions() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<ChatSession> sessions = chatSessionMapper.getUserActiveSessions(userId);
        
        return sessions.stream().map(session -> {
            Map<String, Object> sessionInfo = new HashMap<>();
            sessionInfo.put("id", session.getId());
            sessionInfo.put("title", session.getSessionTitle());
            sessionInfo.put("courseName", session.getCourseName());
            sessionInfo.put("updatedAt", session.getUpdatedAt());
            
            // 获取最新的一条消息作为预览
            List<ChatMessage> recentMessages = chatMessageMapper.getRecentMessages(session.getId(), 1);
            if (!recentMessages.isEmpty()) {
                ChatMessage lastMessage = recentMessages.get(0);
                String preview = lastMessage.getContent();
                if (preview.length() > 50) {
                    preview = preview.substring(0, 50) + "...";
                }
                sessionInfo.put("lastMessage", preview);
                sessionInfo.put("lastMessageRole", lastMessage.getRole());
            }
            
            return sessionInfo;
        }).collect(Collectors.toList());
    }
    
    /**
     * 创建新的聊天会话
     */
    @Transactional
    public Long createNewChatSession(String courseName, Long courseId) {
        Long userId = StpUtil.getLoginIdAsLong();
        
        ChatSession session = new ChatSession()
                .setUserId(userId)
                .setSessionTitle("New Chat")
                .setCourseName(courseName)
                .setCourseId(courseId)
                .setIsActive(true);
        
        chatSessionMapper.insert(session);
        log.info("Created new chat session {} for user {}", session.getId(), userId);
        
        return session.getId();
    }
    
    /**
     * 保存聊天消息
     */
    @Transactional
    public void saveChatMessage(Long sessionId, String role, String content, 
                               List<Map<String, Object>> references, 
                               List<String> knowledgePoints) {
        try {
            // 获取下一个消息序号
            Integer maxOrder = chatMessageMapper.getMaxMessageOrder(sessionId);
            int nextOrder = (maxOrder == null ? 0 : maxOrder) + 1;
            
            // 估算token数量（粗略计算，可以后续优化）
            int tokenCount = estimateTokenCount(content);
            
            ChatMessage message = new ChatMessage()
                    .setSessionId(sessionId)
                    .setRole(role)
                    .setContent(content)
                    .setMessageOrder(nextOrder)
                    .setTokenCount(tokenCount);
            
            // 序列化JSON字段
            if (references != null && !references.isEmpty()) {
                message.setReferences(objectMapper.writeValueAsString(references));
            }
            if (knowledgePoints != null && !knowledgePoints.isEmpty()) {
                message.setKnowledgePoints(objectMapper.writeValueAsString(knowledgePoints));
            }
            
            chatMessageMapper.insert(message);
            
            // 更新会话时间
            chatSessionMapper.updateSessionTime(sessionId);
            
            // 如果是第一条用户消息，更新会话标题
            if (nextOrder == 1 && "user".equals(role)) {
                String title = content.length() > 30 ? content.substring(0, 30) + "..." : content;
                chatSessionMapper.updateSessionTitle(sessionId, title);
            }
            
            // 检查是否需要生成摘要
            checkAndGenerateSummary(sessionId);
            
            log.info("Saved chat message to session {}, role: {}, order: {}", sessionId, role, nextOrder);
            
        } catch (Exception e) {
            log.error("Error saving chat message: ", e);
            throw new RuntimeException("Failed to save chat message", e);
        }
    }
    
    /**
     * 获取会话的记忆上下文
     * 实现类似langchain memory的功能
     */
    public List<Map<String, String>> getSessionMemoryContext(Long sessionId) {
        try {
            // 1. 获取所有摘要
            List<ChatMemorySummary> summaries = chatMemorySummaryMapper.getSessionSummaries(sessionId);
            
            // 2. 获取最近的消息
            List<ChatMessage> recentMessages = chatMessageMapper.getRecentMessages(sessionId, MAX_RECENT_MESSAGES);
            Collections.reverse(recentMessages); // 按时间正序排列
            
            // 3. 计算token数量，确保不超过限制
            int totalTokens = summaries.stream().mapToInt(ChatMemorySummary::getTokenCount).sum() +
                            recentMessages.stream().mapToInt(ChatMessage::getTokenCount).sum();
            
            List<Map<String, String>> context = new ArrayList<>();
            
            // 4. 添加摘要内容
            for (ChatMemorySummary summary : summaries) {
                Map<String, String> summaryMessage = new HashMap<>();
                summaryMessage.put("role", "system");
                summaryMessage.put("content", "Historical context: " + summary.getSummaryContent());
                context.add(summaryMessage);
            }
            
            // 5. 添加最近的消息
            for (ChatMessage message : recentMessages) {
                Map<String, String> messageMap = new HashMap<>();
                messageMap.put("role", message.getRole());
                messageMap.put("content", message.getContent());
                context.add(messageMap);
            }
            
            log.info("Generated memory context for session {}: {} summaries, {} recent messages, {} total tokens", 
                    sessionId, summaries.size(), recentMessages.size(), totalTokens);
            
            return context;
            
        } catch (Exception e) {
            log.error("Error getting session memory context: ", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 删除聊天会话
     */
    @Transactional
    public void deleteChatSession(Long sessionId) {
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 验证会话所有权
        ChatSession session = chatSessionMapper.selectById(sessionId);
        if (session == null || !session.getUserId().equals(userId)) {
            throw new RuntimeException("Session not found or access denied");
        }
        
        // 设置为非活跃状态（软删除）
        session.setIsActive(false);
        chatSessionMapper.updateById(session);
        
        log.info("Deleted chat session {} for user {}", sessionId, userId);
    }
    
    /**
     * 检查并生成会话摘要
     */
    private void checkAndGenerateSummary(Long sessionId) {
        try {
            Integer messageCount = chatMessageMapper.getSessionMessageCount(sessionId);
            if (messageCount == null || messageCount < SUMMARY_TRIGGER_MESSAGES) {
                return;
            }
            
            // 获取最新的摘要信息
            ChatMemorySummary latestSummary = chatMemorySummaryMapper.getLatestSummary(sessionId);
            
            // 确定需要摘要的消息范围
            int startOrder = latestSummary != null ? latestSummary.getMessageRangeEnd().intValue() + 1 : 1;
            int endOrder = messageCount - MAX_RECENT_MESSAGES; // 保留最近的消息不摘要
            
            if (endOrder <= startOrder) {
                return; // 不需要生成摘要
            }
            
            // 获取需要摘要的消息
            List<ChatMessage> messagesToSummarize = chatMessageMapper.getMessagesByRange(sessionId, startOrder, endOrder);
            if (messagesToSummarize.isEmpty()) {
                return;
            }
            
            // 生成摘要
            String summary = generateSummary(messagesToSummarize);
            int summaryTokens = estimateTokenCount(summary);
            
            // 保存摘要
            ChatMemorySummary memorySummary = new ChatMemorySummary()
                    .setSessionId(sessionId)
                    .setSummaryContent(summary)
                    .setMessageRangeStart((long) startOrder)
                    .setMessageRangeEnd((long) endOrder)
                    .setTokenCount(summaryTokens);
            
            chatMemorySummaryMapper.insert(memorySummary);
            
            log.info("Generated summary for session {}, messages {}-{}", sessionId, startOrder, endOrder);
            
        } catch (Exception e) {
            log.error("Error generating summary for session {}: ", sessionId, e);
        }
    }
    
    /**
     * 估算文本的token数量
     * 简单实现：中文按字符数计算，英文按词数计算
     */
    private int estimateTokenCount(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        
        // 粗略估算：中文字符 * 1.5 + 英文词数 * 1.3
        long chineseChars = text.chars().filter(c -> c >= 0x4E00 && c <= 0x9FFF).count();
        long englishWords = text.split("\\s+").length;
        
        return (int) (chineseChars * 1.5 + englishWords * 1.3);
    }
    
    /**
     * 获取会话详情
     */
    public Map<String, Object> getSessionDetail(Long sessionId) {
        Long userId = StpUtil.getLoginIdAsLong();
        
        ChatSession session = chatSessionMapper.selectById(sessionId);
        if (session == null || !session.getUserId().equals(userId) || !session.getIsActive()) {
            throw new RuntimeException("Session not found or access denied");
        }
        
        List<ChatMessage> messages = chatMessageMapper.getSessionMessages(sessionId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("session", session);
        result.put("messages", messages.stream().map(msg -> {
            Map<String, Object> msgMap = new HashMap<>();
            msgMap.put("id", msg.getId());
            msgMap.put("role", msg.getRole());
            msgMap.put("content", msg.getContent());
            msgMap.put("createdAt", msg.getCreatedAt());
            
            // 解析JSON字段
            try {
                if (msg.getReferences() != null) {
                    List<Map<String, Object>> refs = objectMapper.readValue(msg.getReferences(), 
                            new TypeReference<List<Map<String, Object>>>() {});
                    msgMap.put("references", refs);
                }
                if (msg.getKnowledgePoints() != null) {
                    List<String> kps = objectMapper.readValue(msg.getKnowledgePoints(), 
                            new TypeReference<List<String>>() {});
                    msgMap.put("knowledgePoints", kps);
                }
            } catch (Exception e) {
                log.warn("Error parsing JSON fields for message {}: ", msg.getId(), e);
            }
            
            return msgMap;
        }).collect(Collectors.toList()));
        
        return result;
    }
    
    /**
     * 生成对话摘要
     */
    private String generateSummary(List<ChatMessage> messages) {
        try {
            if (messages == null || messages.isEmpty()) {
                return "";
            }
            
            // 构建对话内容
            StringBuilder conversation = new StringBuilder();
            for (ChatMessage message : messages) {
                String role = "user".equals(message.getRole()) ? "学生" : "助教";
                conversation.append(role).append(": ").append(message.getContent()).append("\n");
            }
            
            // 构建摘要提示词
            String prompt = buildSummaryPrompt(conversation.toString());
            
            // 调用AI服务生成摘要
            Map<String, Object> request = new HashMap<>();
            request.put("question", prompt);
            
            Map<String, Object> response = aiServiceCaller.callAiServiceDirectly("/rag/assistant", request);
            
            if (response != null && "success".equals(response.get("status"))) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data != null && data.containsKey("answer")) {
                    String summary = (String) data.get("answer");
                    log.info("Generated summary for {} messages", messages.size());
                    return summary;
                }
            }
            
            // 如果AI生成失败，使用简单的规则生成摘要
            return generateFallbackSummary(messages);
            
        } catch (Exception e) {
            log.error("Error generating summary: ", e);
            return generateFallbackSummary(messages);
        }
    }
    
    /**
     * 构建摘要生成的提示词
     */
    private String buildSummaryPrompt(String conversation) {
        return String.format("""
            请对以下学生与AI助教的对话进行简洁的总结，保留关键信息和学习要点：
            
            对话内容：
            %s
            
            总结要求：
            1. 提取学生的主要问题和关注点
            2. 总结助教提供的核心知识点和解答
            3. 记录重要的学习脉络和概念
            4. 保持简洁，控制在200字以内
            5. 直接返回总结内容，不要额外的格式
            
            请生成对话总结：
            """, conversation);
    }
    
    /**
     * 生成备用摘要（当AI生成失败时使用）
     */
    private String generateFallbackSummary(List<ChatMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return "空对话";
        }
        
        // 提取用户问题
        List<String> userQuestions = messages.stream()
                .filter(msg -> "user".equals(msg.getRole()))
                .map(ChatMessage::getContent)
                .collect(Collectors.toList());
        
        // 简单拼接摘要
        StringBuilder summary = new StringBuilder("讨论了");
        if (!userQuestions.isEmpty()) {
            String firstQuestion = userQuestions.get(0);
            if (firstQuestion.length() > 50) {
                firstQuestion = firstQuestion.substring(0, 50) + "...";
            }
            summary.append("：").append(firstQuestion);
            
            if (userQuestions.size() > 1) {
                summary.append(" 等").append(userQuestions.size()).append("个问题");
            }
        } else {
            summary.append("学习相关问题");
        }
        
        return summary.toString();
    }
}
