package org.example.edusoft.service;

import org.example.edusoft.entity.Class;
import java.util.List;

public interface ClassService {
    Class findById(Long id);
    Class findByClassCode(String classCode);
    List<Class> findByCourseId(Long courseId);
    Class createClass(Class clazz);
    Class updateClass(Class clazz);
    void deleteClass(Long id);
} 