package org.example.edusoft.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ResourceVersion {
    private Long id;
    private Long resourceId;
    private Integer version;
    private String fileUrl;
    private LocalDateTime uploadedAt;
} 