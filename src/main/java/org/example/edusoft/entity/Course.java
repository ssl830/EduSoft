package org.example.edusoft.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Course {
    private Long id;
    private Long teacherId;
    private String name;
    private String code;
    private String outline;
    private String objective;
    private String assessment;
    private LocalDateTime createdAt;
    private List<CourseSection> sections;
} 