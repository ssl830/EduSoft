package org.example.edusoft.content.dto.progress;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressStatisticsDTO {
    
    private Long resourceId;
    private String resourceTitle;
    private Long courseId;
    private Long chapterId;
    private Integer totalStudents;
    private Integer completedStudents;
    private Double averageProgress;
    private Double averageWatchTime;
}
