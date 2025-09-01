package org.example.edusoft.learning.service;

import java.util.List;
import java.util.Map;

public interface SelfPracticeService {
    void saveGeneratedPractice(String prompt, String result, Long studentId);
    List<Map<String,Object>> getHistory(Long stuId);
    List<Map<String,Object>> getDetail(Long stuId,Long practiceId);
    boolean checkPracticeExists(Long practiceId);
}
