package org.example.edusoft.service;

import org.example.edusoft.entity.Answer;
import java.util.List;

public interface AnswerService {
    Answer findById(Long id);
    List<Answer> findBySubmissionId(Long submissionId);
    List<Answer> findByQuestionId(Long questionId);
    Answer createAnswer(Answer answer);
    Answer updateAnswer(Answer answer);
    void deleteAnswer(Long id);
} 