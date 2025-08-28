package org.example.edusoft.entity.classroom;

import lombok.Data;
//获取班级详情类
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