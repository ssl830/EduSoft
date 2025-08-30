package org.example.edusoft.learning.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "answer")
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "submission_id", nullable = false)
    private Long submissionId;

    @Column(name = "question_id", nullable = false)
    private Long questionId; 

    @Column(name = "answer_text")
    private String answerText;

    @Column(name = "is_judged")
    private Boolean isJudged;

    private Boolean correct;

    private Integer score;

    @Column(name = "sort_order")
    private Long sortOrder;

    public String getAnswer() {
        return answerText;
    }
}
