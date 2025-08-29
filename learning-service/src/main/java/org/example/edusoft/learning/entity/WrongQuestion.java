package org.example.edusoft.learning.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wrong_question")
public class WrongQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "wrong_answer")
    private String wrongAnswer;

    @Column(name = "correct_answer")
    private String correctAnswer;

    @Column(name = "wrong_count")
    private Integer wrongCount = 1;

    @Column(name = "last_wrong_time")
    private Timestamp lastWrongTime;

    @Column(name = "created_at")
    private Timestamp createdAt;
}
