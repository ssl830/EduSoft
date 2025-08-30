package org.example.edusoft.learning.entity.homework;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Homework {
    private Long id;
    private String title;
    private String description;
    private Long classId;
    private Long createdBy;
    private String attachmentUrl;
    private String objectName;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
