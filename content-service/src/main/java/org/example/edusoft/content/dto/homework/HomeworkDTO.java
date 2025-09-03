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
public class HomeworkDTO {
    
    private Long id;
    private Long classId;
    private String title;
    private String description;
    private String endTime;
    private String fileUrl;
    private String fileName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
}
