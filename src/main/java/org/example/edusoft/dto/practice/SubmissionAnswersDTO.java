package org.example.edusoft.dto.practice;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionAnswersDTO {
    private Long id;
    private Long submissionId;
    private List<QuestionAnswerDTO> question;
} 