package org.example.edusoft.entity.practice;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class QuestionListDTO {
    private Long id;
    private String name;
    private Long courseId;
    private String courseName;
    private Long sectionId;
    private String sectionName;
    private String teacherId;
    private String type;
    private List<Map<String, String>> options;
    private String answer;
} 