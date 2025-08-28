package org.example.edusoft.entity.discussion;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@TableName("DiscussionLike")
public class DiscussionLike {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @NotNull(message = "讨论ID不能为空")
    @TableField("discussion_id")
    private Long discussionId;
    
    @NotNull(message = "用户ID不能为空")
    @TableField("user_id")
    private Long userId;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
} 