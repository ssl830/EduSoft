package org.example.edusoft.entity.chat;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 聊天会话实体类
 */
@Data
@Accessors(chain = true)
@TableName("chat_session")
public class ChatSession {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("session_title")
    private String sessionTitle;
    
    @TableField("course_id")
    private Long courseId;
    
    @TableField("course_name")
    private String courseName;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableField("is_active")
    private Boolean isActive;
}
