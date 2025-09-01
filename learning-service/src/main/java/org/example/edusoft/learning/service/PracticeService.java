package org.example.edusoft.learning.service;

import java.util.List;
import java.util.Map;

import org.example.edusoft.learning.dto.PracticeDTO;
import org.example.edusoft.learning.entity.Practice;
import org.example.edusoft.learning.entity.Question;

public interface PracticeService {
    
    Practice createPractice(Practice practice);

    Practice updatePractice(Practice practice);

    List<Practice> getPracticeList(Long classId);

    Practice getPracticeDetail(Long id);

    void deletePractice(Long id);

    void addQuestionToPractice(Long practiceId, Long questionId, Integer score);

    void removeQuestionFromPractice(Long practiceId, Long questionId);

    List<Question> getPracticeQuestions(Long practiceId);

    void favoriteQuestion(Long studentId, Long questionId);
    
    void unfavoriteQuestion(Long studentId, Long questionId);
    
    List<Map<String, Object>> getFavoriteQuestions(Long studentId);
 
    void addWrongQuestion(Long studentId, Long questionId, String wrongAnswer);
    
    List<Map<String, Object>> getWrongQuestions(Long studentId);
    
    List<Map<String, Object>> getWrongQuestionsByCourse(Long studentId, Long courseId);
    
    void removeWrongQuestion(Long studentId, Long questionId);

    List<PracticeDTO> getStudentPracticeList(Long studentId, Long classId);

    List<Map<String, Object>> getTeacherPractices(Long teacherId);

    /**
     * 获取练习的提交统计信息
     * @param practiceId 练习ID
     * @return 包含总提交人数和平均分的统计信息
     */
    Map<String, Object> getSubmissionStats(Long practiceId);

    /**
     * 练习截止后统计并写入每题得分率
     */
    void updateScoreRateAfterDeadline(Long practiceId);

    /**
     * 获取所有已截止且未统计得分率的练习ID（定时任务用）
     */
    List<Long> getAllEndedPracticeIds();

    // 新增：按课程获取练习列表（当前用于学生端展示，保留studentId参数以便后续扩展个性化）
    List<Map<String, Object>> getCoursePractices(Long studentId, Long courseId);
}
