package org.example.edusoft.service.impl;

import org.example.edusoft.entity.ClassStudent;
import org.example.edusoft.mapper.ClassStudentMapper;
import org.example.edusoft.service.ClassStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClassStudentServiceImpl implements ClassStudentService {
    @Autowired
    private ClassStudentMapper classStudentMapper;

    @Override
    public ClassStudent findByClassAndStudent(Long classId, Long studentId) {
        return classStudentMapper.findByClassAndStudent(classId, studentId);
    }

    @Override
    public List<ClassStudent> findByClassId(Long classId) {
        return classStudentMapper.findByClassId(classId);
    }

    @Override
    public List<ClassStudent> findByStudentId(Long studentId) {
        return classStudentMapper.findByStudentId(studentId);
    }

    @Override
    @Transactional
    public ClassStudent addStudentToClass(ClassStudent classStudent) {
        classStudentMapper.insert(classStudent);
        return classStudent;
    }

    @Override
    @Transactional
    public void removeStudentFromClass(Long classId, Long studentId) {
        classStudentMapper.delete(classId, studentId);
    }
} 