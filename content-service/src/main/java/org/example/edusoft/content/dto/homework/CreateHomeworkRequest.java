package org.example.edusoft.content.dto.homework;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateHomeworkRequest {
    
    private Long classId;
    private String title;
    private String description;
    private String endTime;
}
