package org.example.edusoft.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private String id;
    private String username;
    private String passwordHash;
    private String role;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static final String ROLE_STUDENT = "student";
    public static final String ROLE_TEACHER = "teacher";
    public static final String ROLE_TA = "ta";
} 