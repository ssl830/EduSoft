package org.example.edusoft.service.impl;

import org.example.edusoft.entity.Question;
import org.example.edusoft.mapper.QuestionMapper;
import org.example.edusoft.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public Question findById(Long id) {
        return questionMapper.findById(id);
    }

    @Override
    public List<Question> findByCreatorId(Long creatorId) {
        return questionMapper.findByCreatorId(creatorId);
    }

    @Override
    public List<Question> findByType(Question.QuestionType type) {
        return questionMapper.findByType(type);
    }

    @Override
    @Transactional
    public Question createQuestion(Question question) {
        questionMapper.insert(question);
        return question;
    }

    @Override
    @Transactional
    public Question updateQuestion(Question question) {
        questionMapper.update(question);
        return question;
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id) {
        questionMapper.deleteById(id);
    }
} 