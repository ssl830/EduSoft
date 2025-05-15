package org.example.edusoft.Mapper;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.FavoriteQuestion;
import java.util.List;

@Mapper
public interface FavoriteQuestionMapper {
    @Select("SELECT * FROM FavoriteQuestion WHERE student_id = #{studentId} AND question_id = #{questionId}")
    FavoriteQuestion findByStudentAndQuestion(Long studentId, Long questionId);

    @Select("SELECT * FROM FavoriteQuestion WHERE student_id = #{studentId}")
    List<FavoriteQuestion> findByStudentId(Long studentId);

    @Select("SELECT * FROM FavoriteQuestion WHERE question_id = #{questionId}")
    List<FavoriteQuestion> findByQuestionId(Long questionId);

    @Insert("INSERT INTO FavoriteQuestion (student_id, question_id) VALUES (#{studentId}, #{questionId})")
    int insert(FavoriteQuestion favoriteQuestion);

    @Delete("DELETE FROM FavoriteQuestion WHERE student_id = #{studentId} AND question_id = #{questionId}")
    int delete(Long studentId, Long questionId);
} 