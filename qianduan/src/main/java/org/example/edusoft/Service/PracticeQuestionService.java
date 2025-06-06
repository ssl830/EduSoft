package org.example.edusoft.service;

import org.example.edusoft.entity.PracticeQuestion;
import java.util.List;

public interface PracticeQuestionService {
    PracticeQuestion findByPracticeAndQuestion(Long practiceId, Long questionId);
    List<PracticeQuestion> findByPracticeId(Long practiceId);
    List<PracticeQuestion> findByQuestionId(Long questionId);
    PracticeQuestion addQuestionToPractice(PracticeQuestion practiceQuestion);
    PracticeQuestion updateQuestionScore(PracticeQuestion practiceQuestion);
    void removeQuestionFromPractice(Long practiceId, Long questionId);
} 