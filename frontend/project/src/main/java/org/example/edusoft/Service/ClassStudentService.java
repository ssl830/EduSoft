package org.example.edusoft.service;

import org.example.edusoft.entity.ClassStudent;
import java.util.List;

public interface ClassStudentService {
    ClassStudent findByClassAndStudent(Long classId, Long studentId);
    List<ClassStudent> findByClassId(Long classId);
    List<ClassStudent> findByStudentId(Long studentId);
    ClassStudent addStudentToClass(ClassStudent classStudent);
    void removeStudentFromClass(Long classId, Long studentId);
} 