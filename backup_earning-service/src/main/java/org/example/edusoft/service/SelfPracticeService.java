package org.example.edusoft.learning.service;

import java.util.List;
import java.util.Map;

/**
 * 自我练习服务
 */
public interface SelfPracticeService {
    /**
     * 保存生成的自测练习
     * 
     * @param studentId 学生ID
     * @param aiResult AI生成的练习结果
     * @return 自测练习ID
     */
    Long saveGeneratedPractice(Long studentId, Map<String, Object> aiResult);
    
    /**
     * 获取学生自测历史
     * 
     * @param stuId 学生ID
     * @return 自测历史记录
     */
    List<Map<String,Object>> getHistory(Long stuId);
    
    /**
     * 获取自测详情
     * 
     * @param stuId 学生ID
     * @param practiceId 练习ID
     * @return 自测详情记录
     */
    List<Map<String,Object>> getDetail(Long stuId, Long practiceId);
    
    /**
     * 检查练习是否存在
     * 
     * @param practiceId 练习ID
     * @return 是否存在
     */
    boolean checkPracticeExists(Long practiceId);
}
