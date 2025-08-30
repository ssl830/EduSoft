package org.example.edusoft.learning.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingSubmissionDTO {
    private String studentName;
    private String practiceName;
    private Long submissionId;
}
