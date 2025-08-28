package org.example.edusoft.entity.practice;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FavoriteQuestion")
@IdClass(FavoriteQuestionId.class)
public class FavoriteQuestion {
    @Id
    @Column(name = "student_id")
    private Long studentId;

    @Id
    @Column(name = "question_id")
    private Long questionId;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class FavoriteQuestionId implements java.io.Serializable {
    private Long studentId;
    private Long questionId;
} 