package org.example.edusoft.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.edusoft.learning.entity.SelfPracticeQuestion;

import java.util.List;

@Mapper
public interface SelfPracticeQuestionMapper extends BaseMapper<SelfPracticeQuestion> {
    
    @Select("SELECT * FROM self_practice_question WHERE self_practice_id = #{selfPracticeId} ORDER BY sort_order")
    List<SelfPracticeQuestion> getByPracticeId(@Param("selfPracticeId") Long selfPracticeId);
    
    @Select("SELECT COUNT(*) FROM self_practice_question WHERE self_practice_id = #{selfPracticeId}")
    int countByPracticeId(@Param("selfPracticeId") Long selfPracticeId);
}
