package org.example.edusoft.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ClassStudent {
    private Long classId;
    private String studentId;
    private LocalDateTime joinedAt;
} 