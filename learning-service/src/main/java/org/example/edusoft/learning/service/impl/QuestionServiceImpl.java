package org.example.edusoft.learning.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.edusoft.learning.dto.QuestionDTO;
import org.example.edusoft.learning.entity.Question;
import org.example.edusoft.learning.mapper.QuestionMapper;
import org.example.edusoft.learning.service.QuestionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionMapper questionMapper;

    @Override
    @Transactional
    public Question createQuestion(Question question) {
        questionMapper.createQuestion(question);
        return question;
    }

    @Override
    @Transactional
    public Question updateQuestion(Question question) {
        questionMapper.updateQuestion(question);
        return question;
    }

    @Override
    public List<Question> getQuestionList(Long courseId, Integer page, Integer size) {
        int offset = (page - 1) * size;
        return questionMapper.getQuestionList(courseId, offset, size);
    }

    @Override
    public Question getQuestionDetail(Long id) {
        return questionMapper.getQuestionById(id);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id) {
        questionMapper.deleteQuestion(id);
    }

    @Override
    @Transactional
    public void importQuestionsToPractice(Long practiceId, List<Long> questionIds, List<Integer> scores) {
        for (int i = 0; i < questionIds.size(); i++) {
            questionMapper.addQuestionToPractice(practiceId, questionIds.get(i), scores.get(i));
        }
    }

    @Override
    public List<Question> getQuestionsBySection(Long courseId, Long sectionId) {
        return questionMapper.getQuestionsBySection(courseId, sectionId);
    }

    @Override
    @Transactional
    public List<Question> batchCreateQuestions(List<Question> questions) {
        for (Question question : questions) {
            questionMapper.createQuestion(question);
        }
        return questions;
    }

    @Override
    public List<Question> getQuestionListByTeacherAndSection(Long teacherId, Long courseId, Long sectionId) {
        return questionMapper.getQuestionsByTeacherAndSection(teacherId, courseId, sectionId);
    }

    @Override
    public List<QuestionDTO> getQuestionListByCourse(Long courseId) {
        // This method in original code returns a DTO that is not fully defined.
        // Returning null for now.
        return null;
    }

    @Override
    public List<QuestionDTO> getAllQuestions() {
        // This method in original code returns a DTO that is not fully defined.
        // Returning null for now.
        return null;
    }
}
