package org.example.edusoft.entity.classroom;

import lombok.Data;

@Data
public class TeacherClassDTO {
    private Long classId;
    private String className;
    private String classCode;
    private Long courseId;
    private String courseName;
} 