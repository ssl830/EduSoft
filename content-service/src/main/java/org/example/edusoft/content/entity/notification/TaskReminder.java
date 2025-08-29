package org.example.edusoft.entity.notification;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("task_reminder")
public class TaskReminder {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String title;
    
    private String content;
    
    private LocalDateTime createTime;
    
    private LocalDateTime deadline;
    
    private String priority; // HIGH, MEDIUM, LOW
    
    private Boolean completed;
    
    private LocalDateTime completedTime;
} 