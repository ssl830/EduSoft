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
    
    private Long homeworkId; // 修改为前端期望的字段名
    private Long class_id; // 修改为前端期望的字段名
    private String title;
    private String description;
    private String endTime;
    private String fileUrl;
    private String fileName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
}
