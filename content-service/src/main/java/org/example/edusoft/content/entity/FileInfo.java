package org.example.edusoft.content.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class FileInfo {
    private Long id;
    private String fileName;
    private String originalFileName;
    private String filePath;
    private String fileType; // VIDEO, PPT, CODE, PDF, OTHER
    private Long fileSize;
    private Long uploaderId;
    private String uploaderName;
    private String description;
    private String category;
    private String visibility; // PUBLIC, PRIVATE, CLASS_ONLY
    private String objectName;
    private String fileUrl;
    private String status; // active, deleted
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
