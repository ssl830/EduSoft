package org.example.edusoft.entity.chat;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 聊天记忆摘要实体类
 * 用于存储长对话的摘要信息，节省token
 */
@Data
@Accessors(chain = true)
@TableName("chat_memory_summary")
public class ChatMemorySummary {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("session_id")
    private Long sessionId;
    
    @TableField("summary_content")
    private String summaryContent;
    
    @TableField("message_range_start")
    private Long messageRangeStart;
    
    @TableField("message_range_end")
    private Long messageRangeEnd;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField("token_count")
    private Integer tokenCount;
}
