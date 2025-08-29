package org.example.edusoft.content.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TeachingResource {
    private Long id;
    private String title;
    private String description;
    private String content;
    private Long courseId;
    private Long chapterId;
    private String chapterName;
    private String resourceType;
    private String fileUrl;
    private String objectName;
    private Integer duration;
    private Long authorId;
    private String authorName;
    private String tags;
    private Integer viewCount;
    private Integer downloadCount;
    private String status; // published, draft, archived
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
