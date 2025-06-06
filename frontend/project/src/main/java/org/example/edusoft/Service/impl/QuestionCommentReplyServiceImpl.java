package org.example.edusoft.service.impl;

import org.example.edusoft.entity.QuestionCommentReply;
import org.example.edusoft.mapper.QuestionCommentReplyMapper;
import org.example.edusoft.service.QuestionCommentReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionCommentReplyServiceImpl implements QuestionCommentReplyService {
    @Autowired
    private QuestionCommentReplyMapper questionCommentReplyMapper;

    @Override
    public QuestionCommentReply findById(Long id) {
        return questionCommentReplyMapper.findById(id);
    }

    @Override
    public List<QuestionCommentReply> findByCommentId(Long commentId) {
        return questionCommentReplyMapper.findByCommentId(commentId);
    }

    @Override
    public List<QuestionCommentReply> findByUserId(Long userId) {
        return questionCommentReplyMapper.findByUserId(userId);
    }

    @Override
    @Transactional
    public QuestionCommentReply createReply(QuestionCommentReply reply) {
        questionCommentReplyMapper.insert(reply);
        return reply;
    }

    @Override
    @Transactional
    public QuestionCommentReply updateReply(QuestionCommentReply reply) {
        questionCommentReplyMapper.update(reply);
        return reply;
    }

    @Override
    @Transactional
    public void deleteReply(Long id) {
        questionCommentReplyMapper.deleteById(id);
    }
} 