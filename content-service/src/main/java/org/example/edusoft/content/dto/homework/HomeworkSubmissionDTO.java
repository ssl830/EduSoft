package org.example.edusoft.content.dto.homework;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkSubmissionDTO {
    
    private Long id;
    private Long homeworkId;
    private Long studentId;
    private String studentName;
    private String content;
    private String fileUrl;
    private String fileName;
    private LocalDateTime submittedAt;
    private LocalDateTime updatedAt;
    private String status;
    private String feedback;
    private Integer score;
}
