package org.example.edusoft.service.impl;

import org.example.edusoft.entity.Answer;
import org.example.edusoft.mapper.AnswerMapper;
import org.example.edusoft.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnswerServiceImpl implements AnswerService {
    @Autowired
    private AnswerMapper answerMapper;

    @Override
    public Answer findById(Long id) {
        return answerMapper.findById(id);
    }

    @Override
    public List<Answer> findBySubmissionId(Long submissionId) {
        return answerMapper.findBySubmissionId(submissionId);
    }

    @Override
    public List<Answer> findByQuestionId(Long questionId) {
        return answerMapper.findByQuestionId(questionId);
    }

    @Override
    @Transactional
    public Answer createAnswer(Answer answer) {
        answerMapper.insert(answer);
        return answer;
    }

    @Override
    @Transactional
    public Answer updateAnswer(Answer answer) {
        answerMapper.update(answer);
        return answer;
    }

    @Override
    @Transactional
    public void deleteAnswer(Long id) {
        answerMapper.deleteById(id);
    }
} 