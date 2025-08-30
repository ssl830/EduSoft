package org.example.edusoft.content.dto.homework;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class HomeworkDTO {
    private Long homeworkId;
    private Long courseId;
    private String title;
    private String description;
    private String fileUrl;
    private String fileName;
    private LocalDateTime endTime;
    private Long classId;
    private Long createdBy;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
