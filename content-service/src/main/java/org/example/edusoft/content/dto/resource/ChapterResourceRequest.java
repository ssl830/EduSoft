package org.example.edusoft.content.dto.resource;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterResourceRequest {
    
    private Long studentId;
    private Long chapterId;
}
