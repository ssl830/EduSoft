package org.example.edusoft.service.impl;

import org.example.edusoft.dto.TeacherClassDTO;
import org.example.edusoft.mapper.TeacherClassMapper;
import org.example.edusoft.service.TeacherClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherClassServiceImpl implements TeacherClassService {

    @Autowired
    private TeacherClassMapper teacherClassMapper;

    @Override
    public List<TeacherClassDTO> getClassesByTeacherId(Long teacherId) {
        if (teacherId == null) {
            throw new IllegalArgumentException("教师ID不能为空");
        }
        return teacherClassMapper.getClassesByTeacherId(teacherId);
    }
}
