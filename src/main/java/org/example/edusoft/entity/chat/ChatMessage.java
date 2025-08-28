package org.example.edusoft.entity.chat;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 聊天消息实体类
 */
@Data
@Accessors(chain = true)
@TableName("chat_message")
public class ChatMessage {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("session_id")
    private Long sessionId;
    
    @TableField("role")
    private String role;  // "user" 或 "assistant"
    
    @TableField("content")
    private String content;
    
    @TableField("`references`")
    private String references;  // JSON格式的引用资料
    
    @TableField("knowledge_points")
    private String knowledgePoints;  // JSON格式的知识点
    
    @TableField("message_order")
    private Integer messageOrder;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField("token_count")
    private Integer tokenCount;

    public ChatMessage(String assistant, String s) {
        this.role = assistant;
        this.content = s;
    }

    public ChatMessage() {

    }
}
