package org.example.edusoft.learning.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.edusoft.learning.other.BusinessException;
import org.example.edusoft.learning.dto.PracticeDTO;
import org.example.edusoft.learning.entity.Practice;
import org.example.edusoft.learning.entity.Question;
import org.example.edusoft.learning.mapper.*;
import org.example.edusoft.learning.service.PracticeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PracticeServiceImpl implements PracticeService {

    private final PracticeMapper practiceMapper;
    private final QuestionMapper questionMapper;
    private final SubmissionMapper submissionMapper;
    private final FavoriteQuestionMapper favoriteQuestionMapper;
    private final WrongQuestionMapper wrongQuestionMapper;

    @Override
    @Transactional
    public Practice createPractice(Practice practice) {
        practiceMapper.createPractice(practice);
        return practice;
    }

    @Override
    @Transactional
    public Practice updatePractice(Practice practice) {
        practiceMapper.updatePractice(practice);
        return practice;
    }

    @Override
    public List<Practice> getPracticeList(Long classId) {
        return practiceMapper.getPracticeList(classId);
    }

    @Override
    public Practice getPracticeDetail(Long id) {
        Practice practice = practiceMapper.getPracticeById(id);
        if (practice != null) {
            practice.setQuestions(questionMapper.getQuestionsByPractice(id));
        }
        return practice;
    }

    @Override
    @Transactional
    public void deletePractice(Long id) {
        // 1. 删除练习与题目的关联
        questionMapper.removeAllQuestionsFromPractice(id);
        // 2. 删除提交记录
        submissionMapper.removeSubmissionsByPracticeId(id);
        // 3. 删除练习
        practiceMapper.deletePractice(id);
    }

    @Override
    public void addQuestionToPractice(Long practiceId, Long questionId, Integer score) {
        questionMapper.addQuestionToPractice(practiceId, questionId, score);
    }

    @Override
    public void removeQuestionFromPractice(Long practiceId, Long questionId) {
        questionMapper.removeQuestionFromPractice(practiceId, questionId);
    }

    @Override
    public List<Question> getPracticeQuestions(Long practiceId) {
        return questionMapper.getQuestionsByPractice(practiceId);
    }

    @Override
    public void favoriteQuestion(Long studentId, Long questionId) {
        if (!favoriteQuestionMapper.isQuestionFavorited(studentId, questionId)) {
            favoriteQuestionMapper.insertFavoriteQuestion(studentId, questionId);
        }
    }

    @Override
    public void unfavoriteQuestion(Long studentId, Long questionId) {
        favoriteQuestionMapper.deleteFavoriteQuestion(studentId, questionId);
    }

    @Override
    public List<Map<String, Object>> getFavoriteQuestions(Long studentId) {
        return favoriteQuestionMapper.findFavoriteQuestions(studentId);
    }

    @Override
    public void addWrongQuestion(Long studentId, Long questionId, String wrongAnswer) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new BusinessException("题目不存在");
        }
        if (wrongQuestionMapper.existsWrongQuestion(studentId, questionId)) {
            wrongQuestionMapper.updateWrongQuestion(studentId, questionId, wrongAnswer, question.getAnswer());
        } else {
            wrongQuestionMapper.insertWrongQuestion(studentId, questionId, wrongAnswer, question.getAnswer());
        }
    }

    @Override
    public List<Map<String, Object>> getWrongQuestions(Long studentId) {
        return wrongQuestionMapper.findWrongQuestions(studentId);
    }

    @Override
    public List<Map<String, Object>> getWrongQuestionsByCourse(Long studentId, Long courseId) {
        // This method may require a more complex query joining with course table
        // For now, returning all wrong questions
        return wrongQuestionMapper.findWrongQuestions(studentId);
    }

    @Override
    public void removeWrongQuestion(Long studentId, Long questionId) {
        wrongQuestionMapper.deleteWrongQuestion(studentId, questionId);
    }

    @Override
    public List<PracticeDTO> getStudentPracticeList(Long studentId, Long classId) {
        // This requires a custom query and DTO, which is not fully implemented in the original code.
        // Returning null for now.
        return null;
    }
}
