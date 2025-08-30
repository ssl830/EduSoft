package org.example.edusoft.learning.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "practice_question")
@IdClass(PracticeQuestion.PracticeQuestionId.class)
public class PracticeQuestion {
    @Id
    @Column(name = "practice_id")
    private Long practiceId;

    @Id
    @Column(name = "question_id")
    private Long questionId;

    @Column(nullable = false)
    private Integer score;

    @Column(name = "score_rate")
    private Double scoreRate;

    @Column(name = "sort_order")
    private Long sortOrder;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PracticeQuestionId implements Serializable {
        private Long practiceId;
        private Long questionId;
    }
}
