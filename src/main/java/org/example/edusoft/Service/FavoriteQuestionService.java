package org.example.edusoft.service;

import org.example.edusoft.entity.FavoriteQuestion;
import java.util.List;

public interface FavoriteQuestionService {
    FavoriteQuestion findByStudentAndQuestion(String studentId, Long questionId);
    List<FavoriteQuestion> findByStudentId(String studentId);
    List<FavoriteQuestion> findByQuestionId(Long questionId);
    FavoriteQuestion addFavorite(FavoriteQuestion favoriteQuestion);
    void removeFavorite(String studentId, Long questionId);
} 