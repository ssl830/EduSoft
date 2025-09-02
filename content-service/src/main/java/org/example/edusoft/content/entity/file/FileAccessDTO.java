package org.example.edusoft.content.entity.file;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileAccessDTO {
    
    private Long id;
    private String fileName;
    private String fileUrl;
    private String downloadUrl;
    private String previewUrl;
    private Long fileSize;
    private String fileType;
    private LocalDateTime accessTime;
    private Long userId;
    private Long fileId;
} 