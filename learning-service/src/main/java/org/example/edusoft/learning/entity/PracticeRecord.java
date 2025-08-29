package org.example.edusoft.learning.entity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
public class PracticeRecord {
    private Long id;
    private Long practiceId;
    private Long studentId;
    private LocalDateTime submittedAt;
    private Integer score;
    private String feedback;
    private List<QuestionRecord> questions;
    private String practiceTitle;
    private String courseName;
    private String className;
}
