package org.example.edusoft.content.entity.notification;

import lombok.Data;
import java.time.LocalDateTime;

public class TaskReminder {
    private Long id;
    
    private String userId; // 改为String类型，匹配数据库的varchar(15)
    
    private String title;
    
    private String content;
    
    private LocalDateTime createTime;
    
    private LocalDateTime deadline;
    
    private String priority; // HIGH, MEDIUM, LOW
    
    private Boolean completed;
    
    private LocalDateTime completedTime;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }
    
    public LocalDateTime getCompletedTime() { return completedTime; }
    public void setCompletedTime(LocalDateTime completedTime) { this.completedTime = completedTime; }
} 