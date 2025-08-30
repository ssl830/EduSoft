package org.example.edusoft.learning.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PracticeQuestionMapper {
    @Select("SELECT score FROM practice_question WHERE practice_id = #{practiceId} AND question_id = #{questionId} LIMIT 1")
    Integer getScoreByPracticeIdAndQuestionId(@Param("practiceId") Long practiceId, @Param("questionId") Long questionId);
}
