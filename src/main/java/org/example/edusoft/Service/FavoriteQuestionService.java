package org.example.edusoft.service;

import org.example.edusoft.entity.FavoriteQuestion;
import java.util.List;

public interface FavoriteQuestionService {
    FavoriteQuestion findByStudentAndQuestion(Long studentId, Long questionId);
    List<FavoriteQuestion> findByStudentId(Long studentId);
    List<FavoriteQuestion> findByQuestionId(Long questionId);
    FavoriteQuestion addFavorite(FavoriteQuestion favoriteQuestion);
    void removeFavorite(Long studentId, Long questionId);
} 