package org.example.edusoft.entity.practice;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Submission")
public class Submission {
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
} 