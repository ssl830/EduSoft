package org.example.edusoft.service.practice;

import org.example.edusoft.entity.practice.Practice;
import org.example.edusoft.entity.practice.Question;
import java.util.List;
import java.util.Map;

public interface PracticeService {
    
    /**
     * 创建练习
     * @param practice 练习信息
     * @return 创建后的练习对象
     */
    Practice createPractice(Practice practice);

    /**
     * 更新练习信息
     * @param practice 练习信息
     * @return 更新后的练习对象
     */
    Practice updatePractice(Practice practice);

    /**
     * 获取练习列表
     * @param courseId 课程ID
     * @param classId 班级ID
     * @param page 页码
     * @param size 每页大小
     * @return 练习列表
     */
    List<Practice> getPracticeList(Long courseId, Long classId, Integer page, Integer size);

    /**
     * 获取练习详情
     * @param id 练习ID
     * @return 练习详情，包含题目列表
     */
    Practice getPracticeDetail(Long id);

    /**
     * 删除练习
     * @param id 练习ID
     */
    void deletePractice(Long id);

    /**
     * 添加题目到练习
     * @param practiceId 练习ID
     * @param questionId 题目ID
     * @param score 分值
     */
    void addQuestionToPractice(Long practiceId, Long questionId, Integer score);

    /**
     * 从练习中移除题目
     * @param practiceId 练习ID
     * @param questionId 题目ID
     */
    void removeQuestionFromPractice(Long practiceId, Long questionId);

    /**
     * 获取练习的题目列表
     * @param practiceId 练习ID
     * @return 题目列表
     */
    List<Question> getPracticeQuestions(Long practiceId);

    // 收藏题目
    void favoriteQuestion(Long studentId, Long questionId);
    
    // 取消收藏题目
    void unfavoriteQuestion(Long studentId, Long questionId);
    
    // 获取收藏的题目列表
    List<Map<String, Object>> getFavoriteQuestions(Long studentId);
 
    // 添加错题
    void addWrongQuestion(Long studentId, Long questionId, String wrongAnswer);
    
    // 获取错题列表
    List<Map<String, Object>> getWrongQuestions(Long studentId);
    
    // 获取某个课程的错题列表
    List<Map<String, Object>> getWrongQuestionsByCourse(Long studentId, Long courseId);
    
    // 删除错题
    void removeWrongQuestion(Long studentId, Long questionId);

    // 获取课程的所有练习
    List<Map<String, Object>> getCoursePractices(Long studentId, Long courseId);
} 