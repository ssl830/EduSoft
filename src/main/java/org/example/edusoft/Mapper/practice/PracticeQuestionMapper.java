package org.example.edusoft.mapper.practice;

import java.util.List;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.practice.*;

@Mapper
public interface PracticeQuestionMapper {
    @Select("SELECT q.* FROM practicequestion pq JOIN question q ON pq.question_id = q.id WHERE pq.practice_id = #{practiceId}")
    List<Question> findByPracticeId(Long practiceId);

    @Select("SELECT * FROM practicequestion pq WHERE pq.practice_id = #{practiceId}")
    List<PracticeQuestion> findpqByPracticeId(Long practiceId);

    /**
     * 按照sort_order字段顺序获取练习中的题目
     */
    @Select("SELECT * FROM practicequestion WHERE practice_id = #{practiceId} ORDER BY sort_order ASC")
    List<PracticeQuestion> findpqByPracticeIdOrdered(Long practiceId);

    /**
     * 获取练习中指定题目的信息
     */
    @Select("SELECT * FROM practicequestion WHERE practice_id = #{practiceId} AND question_id = #{questionId}")
    PracticeQuestion findByPracticeIdAndQuestionId(@Param("practiceId") Long practiceId, @Param("questionId") Long questionId);

    /**
     * 更新指定练习题的得分率
     */
    @Update("UPDATE practicequestion SET score_rate = #{scoreRate} WHERE practice_id = #{practiceId} AND question_id = #{questionId}")
    int updateScoreRate(@Param("practiceId") Long practiceId, @Param("questionId") Long questionId, @Param("scoreRate") Double scoreRate);
}
