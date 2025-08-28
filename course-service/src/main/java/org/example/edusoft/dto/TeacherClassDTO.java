package org.example.edusoft.dto;

import lombok.Data;

@Data
public class TeacherClassDTO {
    private Long id;
    private String name;
    private String classCode;
    private Integer studentCount;
}
