package org.example.edusoft.learning.entity.homework;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HomeworkSubmission {
    private Long id;
    private Long homeworkId;
    private Long studentId;
    private String studentName;
    private String objectName;
    private String fileUrl;
    private LocalDateTime submittedAt;
}
