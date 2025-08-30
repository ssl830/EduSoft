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
@Table(name = "favorite_question")
@IdClass(FavoriteQuestion.FavoriteQuestionId.class)
public class FavoriteQuestion {
    @Id
    @Column(name = "student_id")
    private Long studentId;

    @Id
    @Column(name = "question_id")
    private Long questionId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FavoriteQuestionId implements Serializable {
        private Long studentId;
        private Long questionId;
    }
}
