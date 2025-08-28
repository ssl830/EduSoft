package org.example.edusoft.entity.discussion;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@TableName("Discussion")
public class Discussion {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("course_id")
    private Long courseId;
    
    @TableField("class_id")
    private Long classId;
    
    @NotNull(message = "创建者ID不能为空")
    @TableField("creator_id")
    private Long creatorId;
    
    @NotNull(message = "用户编号不能为空")
    @TableField("creator_num")
    private String creatorNum;
    
    @NotBlank(message = "标题不能为空")
    @Size(min = 2, max = 200, message = "标题长度必须在2-200个字符之间")
    private String title;
    
    @NotBlank(message = "内容不能为空")
    private String content;
    
    @TableField("is_pinned")
    private Boolean isPinned = false;
    
    @TableField("is_closed")
    private Boolean isClosed = false;
    
    @TableField("view_count")
    private Integer viewCount = 0;
    
    @TableField("reply_count")
    private Integer replyCount = 0;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
} 