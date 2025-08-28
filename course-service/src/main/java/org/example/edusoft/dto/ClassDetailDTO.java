package org.example.edusoft.dto;

import lombok.Data;

@Data
public class ClassDetailDTO {
    private Long id;
    private Long courseId;
    private String courseName;
    private Long teacherId;
    private String teacherName;
    private String className;
    private String classCode;
}
