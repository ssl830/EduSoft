package org.example.edusoft.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.edusoft.learning.entity.SelfSubmission;

@Mapper
public interface SelfSubmissionMapper extends BaseMapper<SelfSubmission> {
    void insertSubmission(org.example.edusoft.learning.entity.SelfSubmission submission);
}