package org.example.edusoft.service.classroom;

import org.example.edusoft.common.exception.BusinessException;
import org.example.edusoft.entity.classroom.Class;
import org.example.edusoft.entity.classroom.ClassDetailDTO;
import org.example.edusoft.entity.classroom.ClassUser;
import org.example.edusoft.entity.imports.ImportRecord;
import org.example.edusoft.mapper.classroom.ClassMapper;
import org.example.edusoft.mapper.classroom.ClassUserMapper;
import org.example.edusoft.mapper.course.CourseMapper;
import org.example.edusoft.mapper.course.CourseClassMapper;
import org.example.edusoft.mapper.imports.ImportRecordMapper;
import org.example.edusoft.service.classroom.impl.ClassServiceImpl; // 修复导入路径
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
class ClassServiceTest {

    @Mock
    private ClassMapper classMapper;

    @Mock
    private ClassUserMapper classUserMapper;

    @Mock
    private CourseMapper courseMapper;

    @Mock
    private CourseClassMapper courseClassMapper;

    @Mock
    private ImportRecordMapper importRecordMapper;

    @InjectMocks
    private ClassServiceImpl classService; // 确保使用正确的实现类

    @Test
    @DisplayName("创建班级 - 成功")
    void testCreateClass_success() {
        Class clazz = new Class();
        clazz.setCourseId(1L);
        clazz.setClassCode("C001");
        clazz.setName("班级1");
        clazz.setCreatorId(100L);

        // mock课程
        org.example.edusoft.entity.course.Course course = new org.example.edusoft.entity.course.Course();
        course.setTeacherId(101L);
        when(courseMapper.selectById(1L)).thenReturn(course);
        // mock插入班级
        when(classMapper.insert(clazz)).thenReturn(1); // insert返回int类型
        // mock插入课程班级关联（假设返回int类型，如果实���为void则用doNothing）
        when(courseClassMapper.insertCourseClass(1L, clazz.getId())).thenReturn(1);
        // mock插入班级用户
        when(classUserMapper.insert(any(ClassUser.class))).thenReturn(1);

        Class result = classService.createClass(clazz);

        assertNotNull(result);
        verify(classMapper, times(1)).insert(clazz);
        verify(courseClassMapper, times(1)).insertCourseClass(1L, clazz.getId());
        verify(classUserMapper, times(2)).insert(any(ClassUser.class));
    }

    @Test
    @DisplayName("创建班级 - 课程不存在")
    void testCreateClass_courseNotFound() {
        Class clazz = new Class();
        clazz.setCourseId(1L);
        clazz.setClassCode("C001");
        clazz.setName("班级1");
        clazz.setCreatorId(100L);

        when(courseMapper.selectById(1L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> classService.createClass(clazz));
        assertEquals(404, exception.getCode());
        assertEquals("课程不存在", exception.getMessage());
    }

    @Test
    @DisplayName("获取教师班级列表 - 有班级")
    void testGetClassesByTeacherId_withClasses() {
        when(classMapper.getClassesByTeacherId(1L)).thenReturn(Arrays.asList(new Class(), new Class()));

        List<Class> result = classService.getClassesByTeacherId(1L);

        assertEquals(2, result.size());
        verify(classMapper, times(1)).getClassesByTeacherId(1L);
    }

    @Test
    @DisplayName("获取教师班级列表 - 无班级")
    void testGetClassesByTeacherId_noClasses() {
        when(classMapper.getClassesByTeacherId(1L)).thenReturn(Collections.emptyList());

        List<Class> result = classService.getClassesByTeacherId(1L);

        assertTrue(result.isEmpty());
        verify(classMapper, times(1)).getClassesByTeacherId(1L);
    }

    @Test
    @DisplayName("获取班级详细信息 - 成功")
    void testGetClassDetailById_success() {
        Class clazz = new Class();
        clazz.setId(1L);
        when(classMapper.selectById(1L)).thenReturn(clazz);
        when(classMapper.getClassDetailById(1L)).thenReturn(new ClassDetailDTO());

        ClassDetailDTO result = classService.getClassDetailById(1L);

        assertNotNull(result);
        verify(classMapper, times(1)).selectById(1L);
        verify(classMapper, times(1)).getClassDetailById(1L);
    }

    @Test
    @DisplayName("获取班级详细信息 - 班级不存在")
    void testGetClassDetailById_classNotFound() {
        when(classMapper.selectById(1L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> classService.getClassDetailById(1L));
        assertEquals(404, exception.getCode());
        assertEquals("班级不存在", exception.getMessage());
    }

    @Test
    @DisplayName("删除班级 - 成功")
    void testDeleteClass_success() {
        when(classMapper.selectById(1L)).thenReturn(new Class());
        when(classMapper.deleteById(1L)).thenReturn(1);

        boolean result = classService.deleteClass(1L);

        assertTrue(result);
        verify(classUserMapper, times(1)).delete(any());
        verify(classMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("删除班级 - 班级不存在")
    void testDeleteClass_classNotFound() {
        when(classMapper.selectById(1L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> classService.deleteClass(1L));
        assertEquals(404, exception.getCode());
        assertEquals("班级不存在", exception.getMessage());
    }

    @Test
    @DisplayName("学生加入班级 - 成功")
    void testJoinClass_success() {
        when(classMapper.selectById(1L)).thenReturn(new Class());
        when(classUserMapper.checkUserInClass(1L, 100L)).thenReturn(0);
        when(classUserMapper.insert(any(ClassUser.class))).thenReturn(1);

        boolean result = classService.joinClass(1L, 100L);

        assertTrue(result);
        verify(classUserMapper, times(1)).insert(any(ClassUser.class));
    }

    @Test
    @DisplayName("学生加入班级 - 已在班级中")
    void testJoinClass_alreadyInClass() {
        when(classMapper.selectById(1L)).thenReturn(new Class());
        when(classUserMapper.checkUserInClass(1L, 100L)).thenReturn(1);

        BusinessException exception = assertThrows(BusinessException.class, () -> classService.joinClass(1L, 100L));
        assertEquals(400, exception.getCode());
        assertEquals("学生已经在班级中", exception.getMessage());
    }

    @Test
    @DisplayName("学生退出班级 - 成功")
    void testLeaveClass_success() {
        when(classMapper.selectById(1L)).thenReturn(new Class());
        when(classUserMapper.checkUserInClass(1L, 100L)).thenReturn(1);
        when(classUserMapper.delete(any())).thenReturn(1);

        boolean result = classService.leaveClass(1L, 100L);

        assertTrue(result);
        verify(classUserMapper, times(1)).delete(any());
    }

    @Test
    @DisplayName("学生退出班级 - 不在班级中")
    void testLeaveClass_notInClass() {
        when(classMapper.selectById(1L)).thenReturn(new Class());
        when(classUserMapper.checkUserInClass(1L, 100L)).thenReturn(0);

        BusinessException exception = assertThrows(BusinessException.class, () -> classService.leaveClass(1L, 100L));
        assertEquals(400, exception.getCode());
        assertEquals("学生不在班级中", exception.getMessage());
    }
}
