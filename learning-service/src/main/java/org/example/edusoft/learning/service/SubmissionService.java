package org.example.edusoft.learning.service;

import org.example.edusoft.learning.Result;
import org.example.edusoft.learning.dto.SubmissionDTO;

import java.util.List;

public interface SubmissionService {
    Result<Long> submitAndAutoJudge(Long practiceId, Long studentId, List<String> answers);
}
