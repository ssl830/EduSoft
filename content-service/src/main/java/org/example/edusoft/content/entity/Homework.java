package org.example.edusoft.content.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Homework {
    private Long id;
    private String title;
    private String description;
    private Long courseId;
    private Long chapterId;
    private String chapterName;
    private Long classId;
    private Long createdBy;
    private String createdByName;
    private String attachmentUrl;
    private String objectName;
    private String fileName;
    private LocalDateTime deadline;
    private String status; // draft, published, archived
    private Integer submissionCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
