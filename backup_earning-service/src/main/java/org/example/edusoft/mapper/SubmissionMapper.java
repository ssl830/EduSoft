package org.example.edusoft.learning.mapper.practice;

import org.apache.ibatis.annotations.Mapper;
import org.example.edusoft.learning.entity.practice.Submission;
import java.util.List;

@Mapper
public interface SubmissionMapper {
    void insert(Submission submission);
    Submission selectById(Long id);
    List<Submission> findByPracticeId(Long practiceId);
    List<Long> findSubmissionIdsByPracticeId(Long practiceId);
    void removeSubmissionsByPracticeId(Long practiceId);
    void update(Submission submission);
    List<Submission> findByPracticeIdWithUnjudgedAnswers(Long practiceId);
    Submission selectOne(Object queryWrapper); // 兼容MyBatis-Plus用法
}
