package org.example.edusoft.entity.record;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class StudyRecord {
    private Long id;
    private Long resourceId;
    private Long studentId;
    private Double progress;
    private Integer lastPosition;
    private Integer watchCount;
    private LocalDateTime lastWatchTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 关联字段
    private String resourceTitle;
    private String courseName;
    private String sectionTitle;
    
    // 用于Excel导出的格式化方法
    public String getFormattedProgress() {
        return String.format("%.2f%%", progress);
    }
    
    public String getFormattedLastWatchTime() {
        return lastWatchTime != null ? 
            lastWatchTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : 
            "";
    }
}