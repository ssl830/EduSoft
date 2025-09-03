package org.example.edusoft.content.dto.homework;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeworkSubmissionDTO {
    
    private Long submissionId;
    private String studentId;
    private String studentName;
    private String fileUrl;
    private String fileName;
    private String submitTime;
}
