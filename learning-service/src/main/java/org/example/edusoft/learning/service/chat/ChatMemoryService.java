package org.example.edusoft.learning.service.chat;

import lombok.extern.slf4j.Slf4j;
import org.example.edusoft.learning.entity.chat.ChatMessage;
import org.example.edusoft.learning.service.ai.AiServiceCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 聊天记忆服务
 * 负责生成对话摘要和管理记忆
 */
@Slf4j
@Service
public class ChatMemoryService {
    
    @Autowired
    @Lazy
    private AiServiceCaller aiServiceCaller;
    
    /**
     * 生成对话摘要
     */
    public String generateSummary(List<ChatMessage> messages) {
        try {
            // 构建对话内容
            String conversation = messages.stream()
                    .map(msg -> msg.getRole() + ": " + msg.getContent())
                    .collect(Collectors.joining("\n"));
            
            // 构建请求
            Map<String, Object> request = new HashMap<>();
            request.put("question", buildSummaryPrompt(conversation));
            request.put("mode", "summary");
            
            // 调用AI服务生成摘要
            Map<String, Object> response = aiServiceCaller.callAiServiceDirectly("/rag/assistant", request);
            
            if ("success".equals(response.get("status"))) {
                return (String) response.get("answer");
            } else {
                log.warn("AI summary generation failed, using fallback");
                return generateFallbackSummary(messages);
            }
            
        } catch (Exception e) {
            log.error("Error generating summary: {}", e.getMessage());
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
    String generateFallbackSummary(List<ChatMessage> messages) {
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
            if (userQuestions.size() == 1) {
                summary.append(userQuestions.get(0).length() > 50 ? 
                    userQuestions.get(0).substring(0, 50) + "..." : userQuestions.get(0));
            } else {
                summary.append(userQuestions.size()).append("个问题，包括：")
                    .append(userQuestions.get(0).length() > 30 ? 
                        userQuestions.get(0).substring(0, 30) + "..." : userQuestions.get(0))
                    .append("等");
            }
        } else {
            summary.append("一般性交流");
        }
        
        return summary.toString();
    }
}
