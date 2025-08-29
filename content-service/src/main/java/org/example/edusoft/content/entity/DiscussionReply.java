package org.example.edusoft.content.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DiscussionReply {
    private Long id;
    private Long discussionId;
    private Long userId;
    private String userNum;
    private String content;
    private Long parentReplyId; // 父回复ID，用于嵌套回复
    private Boolean isTeacherReply;
    private Integer likeCount;
    private String status; // active, deleted
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
