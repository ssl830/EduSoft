package org.example.edusoft.learning.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JudgeSubmissionRequest {
    private Long submissionId;
    private List<JudgeQuestionRequest> questions;
}
