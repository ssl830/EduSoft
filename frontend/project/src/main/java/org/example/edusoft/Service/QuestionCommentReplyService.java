package org.example.edusoft.service;

import org.example.edusoft.entity.QuestionCommentReply;
import java.util.List;

public interface QuestionCommentReplyService {
    QuestionCommentReply findById(Long id);
    List<QuestionCommentReply> findByCommentId(Long commentId);
    List<QuestionCommentReply> findByUserId(Long userId);
    QuestionCommentReply createReply(QuestionCommentReply reply);
    QuestionCommentReply updateReply(QuestionCommentReply reply);
    void deleteReply(Long id);
} 