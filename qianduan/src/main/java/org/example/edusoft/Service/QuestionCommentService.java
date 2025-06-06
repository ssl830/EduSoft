package org.example.edusoft.service;

import org.example.edusoft.entity.QuestionComment;
import java.util.List;

public interface QuestionCommentService {
    QuestionComment findById(Long id);
    List<QuestionComment> findByQuestionId(Long questionId);
    List<QuestionComment> findByUserId(Long userId);
    QuestionComment createComment(QuestionComment comment);
    QuestionComment updateComment(QuestionComment comment);
    void deleteComment(Long id);
} 