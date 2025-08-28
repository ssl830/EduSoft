package org.example.edusoft.service.chat;

import lombok.extern.slf4j.Slf4j;
import org.example.edusoft.entity.chat.ChatMessage;
import org.example.edusoft.service.ai.AiServiceCaller;
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
