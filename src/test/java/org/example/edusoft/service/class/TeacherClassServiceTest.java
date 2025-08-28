package org.example.edusoft.service.classroom;

import org.example.edusoft.entity.classroom.TeacherClassDTO;
import org.example.edusoft.mapper.classroom.TeacherClassMapper;
import org.example.edusoft.service.classroom.impl.TeacherClassServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherClassServiceTest {

    @Mock
    private TeacherClassMapper teacherClassMapper;

    @InjectMocks
    private TeacherClassServiceImpl teacherClassService;

    @Test
    @DisplayName("根据教师ID获取班级列表 - 有班级")
    void testGetClassesByTeacherId_withClasses() {
        List<TeacherClassDTO> classes = Arrays.asList(new TeacherClassDTO(), new TeacherClassDTO());
        when(teacherClassMapper.getClassesByTeacherId(1L)).thenReturn(classes);

        List<TeacherClassDTO> result = teacherClassService.getClassesByTeacherId(1L);

        assertEquals(2, result.size());
        verify(teacherClassMapper, times(1)).getClassesByTeacherId(1L);
    }

    @Test
    @DisplayName("根据教师ID获取班级列表 - 无班级")
    void testGetClassesByTeacherId_noClasses() {
        when(teacherClassMapper.getClassesByTeacherId(2L)).thenReturn(Collections.emptyList());

        List<TeacherClassDTO> result = teacherClassService.getClassesByTeacherId(2L);

        assertTrue(result.isEmpty());
        verify(teacherClassMapper, times(1)).getClassesByTeacherId(2L);
    }
}

