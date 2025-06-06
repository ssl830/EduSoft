package org.example.edusoft.service.impl;

import org.example.edusoft.entity.FavoriteQuestion;
import org.example.edusoft.mapper.FavoriteQuestionMapper;
import org.example.edusoft.service.FavoriteQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoriteQuestionServiceImpl implements FavoriteQuestionService {
    @Autowired
    private FavoriteQuestionMapper favoriteQuestionMapper;

    @Override
    public FavoriteQuestion findByStudentAndQuestion(Long studentId, Long questionId) {
        return favoriteQuestionMapper.findByStudentAndQuestion(studentId, questionId);
    }

    @Override
    public List<FavoriteQuestion> findByStudentId(Long studentId) {
        return favoriteQuestionMapper.findByStudentId(studentId);
    }

    @Override
    public List<FavoriteQuestion> findByQuestionId(Long questionId) {
        return favoriteQuestionMapper.findByQuestionId(questionId);
    }

    @Override
    @Transactional
    public FavoriteQuestion addFavorite(FavoriteQuestion favoriteQuestion) {
        favoriteQuestionMapper.insert(favoriteQuestion);
        return favoriteQuestion;
    }

    @Override
    @Transactional
    public void removeFavorite(Long studentId, Long questionId) {
        favoriteQuestionMapper.delete(studentId, questionId);
    }
} 