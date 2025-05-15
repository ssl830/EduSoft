package org.example.edusoft.Mapper;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.Question;
import java.util.List;

@Mapper
public interface QuestionMapper {
    @Select("SELECT * FROM Question WHERE id = #{id}")
    Question findById(Long id);

    @Select("SELECT * FROM Question WHERE creator_id = #{creatorId}")
    List<Question> findByCreatorId(Long creatorId);

    @Select("SELECT * FROM Question WHERE type = #{type}")
    List<Question> findByType(Question.QuestionType type);

    @Insert("INSERT INTO Question (creator_id, type, content, options, answer) " +
            "VALUES (#{creatorId}, #{type}, #{content}, #{options}, #{answer})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Question question);

    @Update("UPDATE Question SET content = #{content}, options = #{options}, " +
            "answer = #{answer} WHERE id = #{id}")
    int update(Question question);

    @Delete("DELETE FROM Question WHERE id = #{id}")
    int deleteById(Long id);
} 