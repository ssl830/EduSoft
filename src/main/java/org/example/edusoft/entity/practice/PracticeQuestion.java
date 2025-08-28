package org.example.edusoft.entity.practice;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PracticeQuestion")
@IdClass(PracticeQuestionId.class)
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
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class PracticeQuestionId implements java.io.Serializable {
    private Long practiceId;
    private Long questionId;
} 