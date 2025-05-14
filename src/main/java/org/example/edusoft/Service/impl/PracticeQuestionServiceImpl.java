package org.example.edusoft.service.impl;

import org.example.edusoft.entity.PracticeQuestion;
import org.example.edusoft.mapper.PracticeQuestionMapper;
import org.example.edusoft.service.PracticeQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PracticeQuestionServiceImpl implements PracticeQuestionService {
    @Autowired
    private PracticeQuestionMapper practiceQuestionMapper;

    @Override
    public PracticeQuestion findByPracticeAndQuestion(Long practiceId, Long questionId) {
        return practiceQuestionMapper.findByPracticeAndQuestion(practiceId, questionId);
    }

    @Override
    public List<PracticeQuestion> findByPracticeId(Long practiceId) {
        return practiceQuestionMapper.findByPracticeId(practiceId);
    }

    @Override
    public List<PracticeQuestion> findByQuestionId(Long questionId) {
        return practiceQuestionMapper.findByQuestionId(questionId);
    }

    @Override
    @Transactional
    public PracticeQuestion addQuestionToPractice(PracticeQuestion practiceQuestion) {
        practiceQuestionMapper.insert(practiceQuestion);
        return practiceQuestion;
    }

    @Override
    @Transactional
    public PracticeQuestion updateQuestionScore(PracticeQuestion practiceQuestion) {
        practiceQuestionMapper.update(practiceQuestion);
        return practiceQuestion;
    }

    @Override
    @Transactional
    public void removeQuestionFromPractice(Long practiceId, Long questionId) {
        practiceQuestionMapper.delete(practiceId, questionId);
    }
} 