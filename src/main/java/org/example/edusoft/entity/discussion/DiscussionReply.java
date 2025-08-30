package org.example.edusoft.entity.discussion;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@TableName("DiscussionReply")
public class DiscussionReply {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @NotNull(message = "讨论ID不能为空")
    @TableField("discussion_id")
    private Long discussionId;
    
    @NotNull(message = "用户ID不能为空")
    @TableField("user_id")
    private Long userId;
    
    @NotNull(message = "用户编号不能为空")
    @TableField("user_num")
    private String userNum;
    
    @NotBlank(message = "回复内容不能为空")
    private String content;
    
    @TableField("parent_reply_id")
    private Long parentReplyId;
    
    @TableField("is_teacher_reply")
    private Boolean isTeacherReply = false;
    
    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
} 