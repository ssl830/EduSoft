package org.example.edusoft.service;

import org.example.edusoft.entity.Question;
import java.util.List;

public interface QuestionService {
    Question findById(Long id);
    List<Question> findByCreatorId(Long creatorId);
    List<Question> findByType(Question.QuestionType type);
    Question createQuestion(Question question);
    Question updateQuestion(Question question);
    void deleteQuestion(Long id);
} 