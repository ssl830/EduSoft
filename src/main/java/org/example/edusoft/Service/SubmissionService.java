package org.example.edusoft.service;

import org.example.edusoft.entity.Submission;
import java.util.List;

public interface SubmissionService {
    Submission findById(Long id);
    List<Submission> findByPracticeId(Long practiceId);
    List<Submission> findByStudentId(String studentId);
    List<Submission> findByPracticeAndStudent(Long practiceId, String studentId);
    Submission createSubmission(Submission submission);
    Submission updateSubmission(Submission submission);
    void deleteSubmission(Long id);
} 