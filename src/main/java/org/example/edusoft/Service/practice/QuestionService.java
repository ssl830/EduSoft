package org.example.edusoft.service.practice;

import org.example.edusoft.entity.practice.Question;
import org.example.edusoft.entity.practice.QuestionListDTO;
import java.util.List;
import java.util.Map;

public interface QuestionService {
    
    /**
     * 创建题目
     * @param question 题目信息
     * @return 创建后的题目对象
     */
    Question createQuestion(Question question);

    /**
     * 更新题目信息
     * @param question 题目信息
     * @return 更新后的题目对象
     */
    Question updateQuestion(Question question);

    /**
     * 获取题目列表
     * @param courseId 课程ID
     * @param page 页码
     * @param size 每页大小
     * @return 题目列表
     */
    List<Question> getQuestionList(Long courseId, Integer page, Integer size);

    /**
     * 获取题目详情
     * @param id 题目ID
     * @return 题目详情
     */
    Question getQuestionDetail(Long id);

    /**
     * 删除题目
     * @param id 题目ID
     */
    void deleteQuestion(Long id);

    /**
     * 从题库导入题目到练习
     * @param practiceId 练习ID
     * @param questionIds 题目ID列表
     * @param scores 对应的分值列表
     */
    void importQuestionsToPractice(Long practiceId, List<Long> questionIds, List<Integer> scores);

    /**
     * 获取章节下的题目列表
     * @param courseId 课程ID
     * @param sectionId 章节ID
     * @return 题目列表
     */
    List<Question> getQuestionsBySection(Long courseId, Long sectionId);

    /**
     * 批量创建题目
     * @param questions 题目列表
     * @return 创建后的题目列表
     */
    List<Question> batchCreateQuestions(List<Question> questions);

    /**
     * 获取教师在特定章节下的题目列表
     * @param teacherId 教师ID
     * @param courseId 课程ID
     * @param sectionId 章节ID
     * @return 题目列表
     */
    List<Question> getQuestionListByTeacherAndSection(Long teacherId, Long courseId, Long sectionId);

    /**
     * 获取课程下的题目列表
     * @param courseId 课程ID
     * @return 题目列表DTO
     */
    List<QuestionListDTO> getQuestionListByCourse(Long courseId);

    /**
     * 获取所有题目列表
     * @return 题目列表DTO
     */
    List<QuestionListDTO> getAllQuestions();
} 