package org.example.edusoft.Mapper;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.PracticeQuestion;
import java.util.List;

@Mapper
public interface PracticeQuestionMapper {
    @Select("SELECT * FROM PracticeQuestion WHERE practice_id = #{practiceId} AND question_id = #{questionId}")
    PracticeQuestion findByPracticeAndQuestion(Long practiceId, Long questionId);

    @Select("SELECT * FROM PracticeQuestion WHERE practice_id = #{practiceId}")
    List<PracticeQuestion> findByPracticeId(Long practiceId);

    @Select("SELECT * FROM PracticeQuestion WHERE question_id = #{questionId}")
    List<PracticeQuestion> findByQuestionId(Long questionId);

    @Insert("INSERT INTO PracticeQuestion (practice_id, question_id, score) " +
            "VALUES (#{practiceId}, #{questionId}, #{score})")
    int insert(PracticeQuestion practiceQuestion);

    @Update("UPDATE PracticeQuestion SET score = #{score} " +
            "WHERE practice_id = #{practiceId} AND question_id = #{questionId}")
    int update(PracticeQuestion practiceQuestion);

    @Delete("DELETE FROM PracticeQuestion WHERE practice_id = #{practiceId} AND question_id = #{questionId}")
    int delete(Long practiceId, Long questionId);
} 