package org.example.edusoft.content.dto.homework;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class HomeworkSubmissionDTO {
    private Long submissionId;
    private Long homeworkId;
    private Long studentId;
    private String studentName;
    private String fileUrl;
    private String fileName;
    private String objectName;
    private LocalDateTime submitTime;
}
