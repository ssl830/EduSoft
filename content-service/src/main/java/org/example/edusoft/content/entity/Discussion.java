package org.example.edusoft.content.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Discussion {
    private Long id;
    private Long courseId;
    private Long classId;
    private Long creatorId;
    private String creatorNum;
    private String title;
    private String content;
    private String category; // general, question, share, announcement
    private Boolean isPinned;
    private Boolean isClosed;
    private Integer viewCount;
    private Integer replyCount;
    private Integer likeCount;
    private String status; // active, closed, deleted
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
