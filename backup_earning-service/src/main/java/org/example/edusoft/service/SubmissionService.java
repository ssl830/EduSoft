package org.example.edusoft.learning.service;

import org.example.edusoft.common.domain.Result;
import java.util.List;

/**
 * 练习提交服务
 * 处理学生提交答案和自动评判的业务逻辑
 */
public interface SubmissionService {

    /**
     * 提交练习答案并进行自动评判
     * @param practiceId 练习ID
     * @param studentId 学生ID
     * @param answers 答案列表，按题目顺序排列
     * @return 提交记录ID
     */
    Result<Long> submitAndAutoJudge(Long practiceId, Long studentId, List<String> answers);
}
