package org.example.edusoft.learning.service;

import org.example.edusoft.learning.entity.Question;
import org.example.edusoft.learning.dto.QuestionDTO;
import org.example.edusoft.learning.dto.QuestionListDTO;
import java.util.List;

public interface QuestionService {
    
    Question createQuestion(Question question);

    Question updateQuestion(Question question);

    List<Question> getQuestionList(Long courseId, Integer page, Integer size);

    Question getQuestionDetail(Long id);

    void deleteQuestion(Long id);

    void importQuestionsToPractice(Long practiceId, List<Long> questionIds, List<Integer> scores);

    List<Question> getQuestionsBySection(Long courseId, Long sectionId);

    List<Question> batchCreateQuestions(List<Question> questions);

    List<Question> getQuestionListByTeacherAndSection(Long teacherId, Long courseId, Long sectionId);

    List<QuestionListDTO> getQuestionListByCourse(Long courseId);

    List<QuestionListDTO> getAllQuestions();
}
