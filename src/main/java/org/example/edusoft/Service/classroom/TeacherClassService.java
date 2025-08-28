package org.example.edusoft.service.classroom;

import org.example.edusoft.entity.classroom.TeacherClassDTO;
import java.util.List;
 
public interface TeacherClassService {
    List<TeacherClassDTO> getClassesByTeacherId(Long teacherId);
} 