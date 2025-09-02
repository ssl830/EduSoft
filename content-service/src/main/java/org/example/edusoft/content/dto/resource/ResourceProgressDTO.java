package org.example.edusoft.content.dto.resource;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceProgressDTO {
    
    private Long id;
    private String title;
    private String description;
    private String type;
    private String url;
    private Long fileSize;
    private Integer duration;
    private Long courseId;
    private Long chapterId;
    private Long creatorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 学习进度相关字段
    private Double progress;
    private Integer position;
    private LocalDateTime lastAccessedAt;
}
