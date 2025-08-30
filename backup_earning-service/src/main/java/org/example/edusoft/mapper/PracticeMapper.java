package org.example.edusoft.learning.mapper.practice;

import org.apache.ibatis.annotations.Mapper;
import org.example.edusoft.learning.entity.practice.Practice;
import java.util.List;
import java.util.Map;

@Mapper
public interface PracticeMapper {
    void createPractice(Practice practice);
    Practice getPracticeById(Long id);
    List<Practice> getPracticeList(Long classId);
    void updatePractice(Practice practice);
    void deletePractice(Long id);
    List<Long> findAllEndedPracticeIds(java.time.LocalDateTime now);
    List<Long> getClassStudentIds(Long classId);
    List<Map<String, Object>> findFavoriteQuestions(Long studentId);
    boolean isQuestionFavorited(Long studentId, Long questionId);
    void insertFavoriteQuestion(Long studentId, Long questionId);
    void deleteFavoriteQuestion(Long studentId, Long questionId);
    boolean existsWrongQuestion(Long studentId, Long questionId);
    void updateWrongQuestion(Long studentId, Long questionId, String wrongAnswer, String correctAnswer);
    void insertWrongQuestion(Long studentId, Long questionId, String wrongAnswer, String correctAnswer);
    List<Map<String, Object>> findWrongQuestions(Long studentId);
    List<Map<String, Object>> findWrongQuestionsByCourse(Long studentId, Long courseId);
    void deleteWrongQuestion(Long studentId, Long questionId);
    Long findClassIdByUserAndCourse(Long studentId, Long courseId);
    List<Map<String, Object>> findCoursePractices(Long courseId, Long classId, Long studentId);
    List<org.example.edusoft.learning.entity.practice.PracticeListDTO> getStudentPracticeList(Long studentId, Long classId);
    List<Map<String, Object>> getPracticesByTeacherId(Long teacherId);
    List<Practice> findByClassId(Long classId);
}
