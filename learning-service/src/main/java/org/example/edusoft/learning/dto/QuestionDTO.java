package org.example.edusoft.learning.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.example.edusoft.learning.entity.Question;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private Long id;
    private Long creatorId;
    private Question.QuestionType type;
    private String content;
    private String analysis;
    private List<String> optionsList;
    private String answer;
    private Long courseId;
    private Long sectionId;
    private Integer score;
}
