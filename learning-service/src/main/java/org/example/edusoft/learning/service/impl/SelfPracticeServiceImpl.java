package org.example.edusoft.learning.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.edusoft.learning.mapper.SelfPracticeMapper;
import org.example.edusoft.learning.service.SelfPracticeService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SelfPracticeServiceImpl implements SelfPracticeService {

    private final SelfPracticeMapper selfPracticeMapper;

    @Override
    public Long saveGeneratedPractice(Long studentId, Map<String, Object> aiResult) {
        // This method requires complex logic involving AI results.
        // Returning a dummy ID for now.
        return 1L;
    }

    @Override
    public List<Map<String, Object>> getHistory(Long stuId) {
        return selfPracticeMapper.getHistory(stuId);
    }

    @Override
    public List<Map<String, Object>> getDetail(Long stuId, Long practiceId) {
        return selfPracticeMapper.getDetail(stuId, practiceId);
    }

    @Override
    public boolean checkPracticeExists(Long practiceId) {
        return selfPracticeMapper.checkPracticeExists(practiceId) > 0;
    }
}
