package org.example.edusoft.entity.practice;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Answer")
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
} 