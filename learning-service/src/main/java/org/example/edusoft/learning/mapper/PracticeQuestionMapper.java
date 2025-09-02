package org.example.edusoft.learning.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.edusoft.learning.entity.PracticeQuestion;

import java.util.List;

@Mapper
public interface PracticeQuestionMapper {
    @Select("SELECT score FROM practice_question WHERE practice_id = #{practiceId} AND question_id = #{questionId} LIMIT 1")
    Integer getScoreByPracticeIdAndQuestionId(@Param("practiceId") Long practiceId, @Param("questionId") Long questionId);

    @Select("SELECT * FROM practice_question pq WHERE pq.practice_id = #{practiceId}")
    List<PracticeQuestion> findpqByPracticeId(Long practiceId);

    @Select("SELECT practice_id FROM practice_question WHERE question_id = #{questionId}")
    List<Long> findPracticeIdsByQuestionId(@Param("questionId") Long questionId);

    /**
     * 更新指定练习题的得分率
     */
    @Update("UPDATE practice_question SET score_rate = #{scoreRate} WHERE practice_id = #{practiceId} AND question_id = #{questionId}")
    int updateScoreRate(@Param("practiceId") Long practiceId, @Param("questionId") Long questionId, @Param("scoreRate") Double scoreRate);
}
