package org.example.edusoft.service;

import org.example.edusoft.dto.TeacherClassDTO;
import java.util.List;

public interface TeacherClassService {
    List<TeacherClassDTO> getClassesByTeacherId(Long teacherId);
}
