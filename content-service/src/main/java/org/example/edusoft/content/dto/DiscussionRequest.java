package org.example.edusoft.content.dto;

import lombok.Data;

@Data
public class DiscussionRequest {
    private Long courseId;
    private Long classId;
    private Long creatorId;
    private String creatorNum;
    private String title;
    private String content;
    private String category;
    private Boolean isPinned;
    private Boolean isClosed;
}
