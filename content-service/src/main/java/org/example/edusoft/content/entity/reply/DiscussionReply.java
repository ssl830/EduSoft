package org.example.edusoft.content.entity.reply;

import java.time.LocalDateTime;

/**
 * 讨论回复实体类
 */
public class DiscussionReply {
    private Long id;
    private Long discussionId;
    private Long userId;
    private String userNum;
    private String content;
    private Long parentReplyId;
    private Boolean isTeacherReply;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getDiscussionId() { return discussionId; }
    public void setDiscussionId(Long discussionId) { this.discussionId = discussionId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUserNum() { return userNum; }
    public void setUserNum(String userNum) { this.userNum = userNum; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Long getParentReplyId() { return parentReplyId; }
    public void setParentReplyId(Long parentReplyId) { this.parentReplyId = parentReplyId; }
    
    public Boolean getIsTeacherReply() { return isTeacherReply; }
    public void setIsTeacherReply(Boolean isTeacherReply) { this.isTeacherReply = isTeacherReply; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
