package org.example.edusoft.content.dto;

import lombok.Data;

@Data
public class FileUploadRequest {
    private String description;
    private String category;
    private Long uploaderId;
    private String uploaderName;
    private String visibility;
    private String objectName;
    private String fileUrl;
}
