package org.example.edusoft.learning.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionDetailDTO {
    private String questionName;
    private String answerText;
    private Integer maxScore;
    private String studentName;
    private Long sortOrder;
}
