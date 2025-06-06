package org.example.edusoft.service;

import org.example.edusoft.entity.QuestionCommentReplyLike;
import java.util.List;

public interface QuestionCommentReplyLikeService {
    QuestionCommentReplyLike findByUserAndReply(Long userId, Long replyId);
    List<QuestionCommentReplyLike> findByReplyId(Long replyId);
    List<QuestionCommentReplyLike> findByUserId(Long userId);
    QuestionCommentReplyLike addLike(QuestionCommentReplyLike like);
    void removeLike(Long userId, Long replyId);
} 