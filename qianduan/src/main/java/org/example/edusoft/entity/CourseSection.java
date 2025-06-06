package org.example.edusoft.entity;

import lombok.Data;

@Data
public class CourseSection {
    private Long id;
    private Long courseId;
    private String title;
    private Integer sortOrder;
} 