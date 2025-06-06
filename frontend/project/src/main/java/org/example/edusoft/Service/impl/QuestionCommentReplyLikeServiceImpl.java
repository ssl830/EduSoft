package org.example.edusoft.service.impl;

import org.example.edusoft.entity.QuestionCommentReplyLike;
import org.example.edusoft.mapper.QuestionCommentReplyLikeMapper;
import org.example.edusoft.service.QuestionCommentReplyLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionCommentReplyLikeServiceImpl implements QuestionCommentReplyLikeService {
    @Autowired
    private QuestionCommentReplyLikeMapper questionCommentReplyLikeMapper;

    @Override
    public QuestionCommentReplyLike findByUserAndReply(Long userId, Long replyId) {
        return questionCommentReplyLikeMapper.findByUserAndReply(userId, replyId);
    }

    @Override
    public List<QuestionCommentReplyLike> findByReplyId(Long replyId) {
        return questionCommentReplyLikeMapper.findByReplyId(replyId);
    }

    @Override
    public List<QuestionCommentReplyLike> findByUserId(Long userId) {
        return questionCommentReplyLikeMapper.findByUserId(userId);
    }

    @Override
    @Transactional
    public QuestionCommentReplyLike addLike(QuestionCommentReplyLike like) {
        questionCommentReplyLikeMapper.insert(like);
        return like;
    }

    @Override
    @Transactional
    public void removeLike(Long userId, Long replyId) {
        questionCommentReplyLikeMapper.delete(userId, replyId);
    }
} 