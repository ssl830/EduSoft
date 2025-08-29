package org.example.edusoft.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.edusoft.learning.entity.SelfAnswer;

@Mapper
public interface SelfAnswerMapper extends BaseMapper<SelfAnswer> {
    void insertAnswer(org.example.edusoft.learning.entity.SelfAnswer selfAnswer);
}