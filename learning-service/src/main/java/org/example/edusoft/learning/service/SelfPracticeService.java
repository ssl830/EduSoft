package org.example.edusoft.learning.service;

import java.util.List;
import java.util.Map;

public interface SelfPracticeService {
    Long saveGeneratedPractice(Long studentId, Map<String, Object> aiResult);
    List<Map<String,Object>> getHistory(Long stuId);
    List<Map<String,Object>> getDetail(Long stuId,Long practiceId);
    boolean checkPracticeExists(Long practiceId);
}
