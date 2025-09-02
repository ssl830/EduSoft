package org.example.edusoft.content.dto.progress;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningProgressDTO {
    
    private Long id;
    private Long resourceId;
    private Long studentId;
    private String studentName;
    private Double progress;
    private Integer position;
    private Integer watchCount;
    private LocalDateTime lastAccessedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
