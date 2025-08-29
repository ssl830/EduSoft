package org.example.edusoft.content.dto;

import lombok.Data;

@Data
public class TeachingResourceRequest {
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
    private String status;
}
