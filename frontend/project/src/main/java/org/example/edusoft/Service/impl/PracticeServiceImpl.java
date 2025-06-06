package org.example.edusoft.service.impl;

import org.example.edusoft.entity.Practice;
import org.example.edusoft.mapper.PracticeMapper;
import org.example.edusoft.service.PracticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PracticeServiceImpl implements PracticeService {
    @Autowired
    private PracticeMapper practiceMapper;

    @Override
    public Practice findById(Long id) {
        return practiceMapper.findById(id);
    }

    @Override
    public List<Practice> findByCourseId(Long courseId) {
        return practiceMapper.findByCourseId(courseId);
    }

    @Override
    public List<Practice> findByCreatedBy(Long createdBy) {
        return practiceMapper.findByCreatedBy(createdBy);
    }

    @Override
    @Transactional
    public Practice createPractice(Practice practice) {
        practiceMapper.insert(practice);
        return practice;
    }

    @Override
    @Transactional
    public Practice updatePractice(Practice practice) {
        practiceMapper.update(practice);
        return practice;
    }

    @Override
    @Transactional
    public void deletePractice(Long id) {
        practiceMapper.deleteById(id);
    }
} 