package org.example.edusoft.learning.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 * 提交练习答案的请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionDTO {
    private Long practiceId;
    private Long studentId;
    private List<String> answers;
}
