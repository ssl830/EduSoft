package org.example.edusoft.content.dto.file;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileResponseDTO {
    
    private Long id;
    private String title;
    private String description;
    private String type;
    private String url;
    private Long size;
    private String visibility;
    private Long courseId;
    private Long sectionId;
    private Long uploaderId;
    private String uploaderName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isFolder;
    private Long parentFolderId;
}
