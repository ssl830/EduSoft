package org.example.edusoft.service.impl;

import org.example.edusoft.entity.QuestionCommentLike;
import org.example.edusoft.mapper.QuestionCommentLikeMapper;
import org.example.edusoft.service.QuestionCommentLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionCommentLikeServiceImpl implements QuestionCommentLikeService {
    @Autowired
    private QuestionCommentLikeMapper questionCommentLikeMapper;

    @Override
    public QuestionCommentLike findByUserAndComment(Long userId, Long commentId) {
        return questionCommentLikeMapper.findByUserAndComment(userId, commentId);
    }

    @Override
    public List<QuestionCommentLike> findByCommentId(Long commentId) {
        return questionCommentLikeMapper.findByCommentId(commentId);
    }

    @Override
    public List<QuestionCommentLike> findByUserId(Long userId) {
        return questionCommentLikeMapper.findByUserId(userId);
    }

    @Override
    @Transactional
    public QuestionCommentLike addLike(QuestionCommentLike like) {
        questionCommentLikeMapper.insert(like);
        return like;
    }

    @Override
    @Transactional
    public void removeLike(Long userId, Long commentId) {
        questionCommentLikeMapper.delete(userId, commentId);
    }
} 