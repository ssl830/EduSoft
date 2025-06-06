package org.example.edusoft.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Resource {
    private Long id;
    private Long courseId;
    private Long sectionId;
    private Long uploaderId;
    private String title;
    private ResourceType type;
    private String fileUrl;
    private Visibility visibility;
    private Integer version;
    private LocalDateTime createdAt;

    public enum ResourceType {
        ppt,
        pdf,
        video,
        code,
        other
    }

    public enum Visibility {
        public,
        private,
        class_only
    }
} 