package org.example.edusoft.learning.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "submission")
public class PracticeSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "practice_id", nullable = false)
    private Long practiceId;
 
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "is_judged")
    private int isJudged = 0;

    private Integer score = 0;

    private String feedback;
    
    // 添加课程ID和班级ID字段
    @Column(name = "course_id")
    private Long courseId;
    
    @Column(name = "class_id")
    private Long classId;
}
