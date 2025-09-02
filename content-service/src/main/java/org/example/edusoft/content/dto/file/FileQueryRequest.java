package org.example.edusoft.content.dto.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileQueryRequest {
    
    private Long userId;
    private Long courseId;
    private Long chapter;
    private String type;
    private String title;
    private Boolean isTeacher;
}
