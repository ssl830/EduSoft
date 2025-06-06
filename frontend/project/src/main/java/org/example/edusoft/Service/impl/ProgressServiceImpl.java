package org.example.edusoft.service.impl;

import org.example.edusoft.entity.Progress;
import org.example.edusoft.mapper.ProgressMapper;
import org.example.edusoft.service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProgressServiceImpl implements ProgressService {
    @Autowired
    private ProgressMapper progressMapper;

    @Override
    public Progress findById(Long id) {
        return progressMapper.findById(id);
    }

    @Override
    public List<Progress> findByStudentId(Long studentId) {
        return progressMapper.findByStudentId(studentId);
    }

    @Override
    public List<Progress> findByCourseId(Long courseId) {
        return progressMapper.findByCourseId(courseId);
    }

    @Override
    public List<Progress> findByStudentAndCourse(Long studentId, Long courseId) {
        return progressMapper.findByStudentAndCourse(studentId, courseId);
    }

    @Override
    @Transactional
    public Progress createProgress(Progress progress) {
        progressMapper.insert(progress);
        return progress;
    }

    @Override
    @Transactional
    public Progress updateProgress(Progress progress) {
        progressMapper.update(progress);
        return progress;
    }

    @Override
    @Transactional
    public void deleteProgress(Long id) {
        progressMapper.deleteById(id);
    }
} 