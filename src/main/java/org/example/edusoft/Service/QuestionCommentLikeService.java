package org.example.edusoft.service;

import org.example.edusoft.entity.QuestionCommentLike;
import java.util.List;

public interface QuestionCommentLikeService {
    QuestionCommentLike findByUserAndComment(Long userId, Long commentId);
    List<QuestionCommentLike> findByCommentId(Long commentId);
    List<QuestionCommentLike> findByUserId(Long userId);
    QuestionCommentLike addLike(QuestionCommentLike like);
    void removeLike(Long userId, Long commentId);
} 