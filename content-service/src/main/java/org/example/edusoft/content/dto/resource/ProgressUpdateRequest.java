package org.example.edusoft.content.dto.resource;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressUpdateRequest {
    
    private Long resourceId;
    private Long studentId;
    private Double progress;
    private Integer position;
}
