package org.example.edusoft.learning.entity;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
public class QuestionRecord {
    private Long id;
    private Long sectionId;
    private Long courseId;
    private String content;
    private String type;
    private String options; 
    private String studentAnswer;
    private String correctAnswer;
    private Boolean isCorrect;
    private String analysis;
    private Integer score;
}
