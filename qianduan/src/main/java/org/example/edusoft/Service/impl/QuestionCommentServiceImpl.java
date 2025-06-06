package org.example.edusoft.service.impl;

import org.example.edusoft.entity.QuestionComment;
import org.example.edusoft.mapper.QuestionCommentMapper;
import org.example.edusoft.service.QuestionCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionCommentServiceImpl implements QuestionCommentService {
    @Autowired
    private QuestionCommentMapper questionCommentMapper;

    @Override
    public QuestionComment findById(Long id) {
        return questionCommentMapper.findById(id);
    }

    @Override
    public List<QuestionComment> findByQuestionId(Long questionId) {
        return questionCommentMapper.findByQuestionId(questionId);
    }

    @Override
    public List<QuestionComment> findByUserId(Long userId) {
        return questionCommentMapper.findByUserId(userId);
    }

    @Override
    @Transactional
    public QuestionComment createComment(QuestionComment comment) {
        questionCommentMapper.insert(comment);
        return comment;
    }

    @Override
    @Transactional
    public QuestionComment updateComment(QuestionComment comment) {
        questionCommentMapper.update(comment);
        return comment;
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        questionCommentMapper.deleteById(id);
    }
} 