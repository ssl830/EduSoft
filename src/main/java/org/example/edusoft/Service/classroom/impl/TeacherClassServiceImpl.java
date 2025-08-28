package org.example.edusoft.service.classroom.impl;

import org.example.edusoft.entity.classroom.TeacherClassDTO;
import org.example.edusoft.mapper.classroom.TeacherClassMapper;
import org.example.edusoft.service.classroom.TeacherClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TeacherClassServiceImpl implements TeacherClassService {
    @Autowired
    private TeacherClassMapper teacherClassMapper;

    @Override
    public List<TeacherClassDTO> getClassesByTeacherId(Long teacherId) {
        return teacherClassMapper.getClassesByTeacherId(teacherId);
    }
} 