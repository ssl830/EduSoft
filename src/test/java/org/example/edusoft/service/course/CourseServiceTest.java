package org.example.edusoft.service.course;

import org.example.edusoft.entity.course.Course;
import org.example.edusoft.entity.course.CourseDetailDTO;
import org.example.edusoft.entity.course.CourseSection;
import org.example.edusoft.mapper.course.CourseMapper;
import org.example.edusoft.service.course.impl.CourseServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseMapper courseMapper;

    @InjectMocks
    private CourseServiceImpl courseService;

    @Test
    @DisplayName("创建课程 - 成功")
    void testCreateCourse_success() {
        Course course = new Course();
        course.setCode("C001");
        course.setName("课程1");
        course.setTeacherId(1L);

        when(courseMapper.selectCount(any())).thenReturn(0L);
        when(courseMapper.insert(course)).thenReturn(Integer.valueOf(1));

        Course result = courseService.createCourse(course);

        assertEquals(course, result);
        verify(courseMapper, times(1)).insert(course);
    }

    @Test
    @DisplayName("创建课程 - 课程代码已存在")
    void testCreateCourse_codeExists() {
        Course course = new Course();
        course.setCode("C001");
        course.setName("课程1");
        course.setTeacherId(1L);

        when(courseMapper.selectCount(any())).thenReturn(1L);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> courseService.createCourse(course));
        assertEquals("课程代码已存在", ex.getMessage());
    }

    @Test
    @DisplayName("创建课程 - 课程名称为空")
    void testCreateCourse_nameEmpty() {
        Course course = new Course();
        course.setCode("C001");
        course.setName("");
        course.setTeacherId(1L);

        when(courseMapper.selectCount(any())).thenReturn(0L);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> courseService.createCourse(course));
        assertEquals("课程名称不能为空", ex.getMessage());
    }

    @Test
    @DisplayName("创建课程 - 教师ID为空")
    void testCreateCourse_teacherIdNull() {
        Course course = new Course();
        course.setCode("C001");
        course.setName("课程1");
        course.setTeacherId(null);

        when(courseMapper.selectCount(any())).thenReturn(0L);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> courseService.createCourse(course));
        assertEquals("教师ID不能为空", ex.getMessage());
    }

    @Test
    @DisplayName("获取课程列表 - 正常")
    void testGetCoursesByUserId_success() {
        List<Course> courses = Arrays.asList(new Course(), new Course());
        when(courseMapper.getCoursesByUserId(1L)).thenReturn(courses);

        List<Course> result = courseService.getCoursesByUserId(1L);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("获取课程列表 - 用户ID为空")
    void testGetCoursesByUserId_userIdNull() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> courseService.getCoursesByUserId(null));
        assertEquals("用户ID不能为空", ex.getMessage());
    }

    @Test
    @DisplayName("获取课程详情 - 正常")
    void testGetCourseById_success() {
        Course course = new Course();
        course.setId(1L);
        when(courseMapper.selectById(1L)).thenReturn(course);

        Course result = courseService.getCourseById(1L);

        assertEquals(course, result);
    }

    @Test
    @DisplayName("获取课程详情 - 课程ID为空")
    void testGetCourseById_idNull() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> courseService.getCourseById(null));
        assertEquals("课程ID不能为空", ex.getMessage());
    }

    @Test
    @DisplayName("获取课程详情 - 课程不存在")
    void testGetCourseById_notExist() {
        when(courseMapper.selectById(2L)).thenReturn(null);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> courseService.getCourseById(2L));
        assertEquals("课程不存在", ex.getMessage());
    }

    @Test
    @DisplayName("更新课程 - 成功")
    void testUpdateCourse_success() {
        Course course = new Course();
        course.setId(1L);
        course.setCode("C001");
        course.setName("课程1");
        course.setTeacherId(2L);

        Course existing = new Course();
        existing.setId(1L);
        existing.setCode("C001");
        existing.setTeacherId(2L);

        when(courseMapper.selectById(1L)).thenReturn(existing);
        when(courseMapper.updateById(course)).thenReturn(Integer.valueOf(1));

        Course result = courseService.updateCourse(course);

        assertEquals(existing.getTeacherId(), result.getTeacherId());
        verify(courseMapper, times(1)).updateById(course);
    }

    @Test
    @DisplayName("更新课程 - 课程ID为空")
    void testUpdateCourse_idNull() {
        Course course = new Course();
        course.setId(null);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> courseService.updateCourse(course));
        assertEquals("课程ID不能为空", ex.getMessage());
    }

    @Test
    @DisplayName("更新课程 - 课程不存在")
    void testUpdateCourse_notExist() {
        Course course = new Course();
        course.setId(2L);
        course.setCode("C002");

        when(courseMapper.selectById(2L)).thenReturn(null);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> courseService.updateCourse(course));
        assertEquals("课程不存在", ex.getMessage());
    }

    @Test
    @DisplayName("更新课程 - 课程代码已存在")
    void testUpdateCourse_codeExists() {
        Course course = new Course();
        course.setId(1L);
        course.setCode("C002");

        Course existing = new Course();
        existing.setId(1L);
        existing.setCode("C001");
        existing.setTeacherId(2L);

        when(courseMapper.selectById(1L)).thenReturn(existing);
        when(courseMapper.selectCount(any())).thenReturn(1L);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> courseService.updateCourse(course));
        assertEquals("课程代码已存在", ex.getMessage());
    }

    @Test
    @DisplayName("删除课程 - 成功")
    void testDeleteCourse_success() {
        Course course = new Course();
        course.setId(1L);
        when(courseMapper.selectById(1L)).thenReturn(course);
        when(courseMapper.deleteById(1L)).thenReturn(Integer.valueOf(1));

        boolean result = courseService.deleteCourse(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("删除课程 - 课程ID为空")
    void testDeleteCourse_idNull() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> courseService.deleteCourse(null));
        assertEquals("课程ID不能为空", ex.getMessage());
    }

    @Test
    @DisplayName("删除课程 - 课程不存在")
    void testDeleteCourse_notExist() {
        when(courseMapper.selectById(2L)).thenReturn(null);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> courseService.deleteCourse(2L));
        assertEquals("课程不存在", ex.getMessage());
    }

    @Test
    @DisplayName("获取课程详细信息 - 正常")
    void testGetCourseDetailById_success() {
        CourseDetailDTO detail = new CourseDetailDTO();
        List<CourseSection> sections = Arrays.asList(new CourseSection());
        List<CourseDetailDTO.ClassInfo> classes = Arrays.asList(new CourseDetailDTO.ClassInfo());

        when(courseMapper.getCourseDetailById(1L)).thenReturn(detail);
        when(courseMapper.getSectionsByCourseId(1L)).thenReturn(sections);
        when(courseMapper.getClassesByCourseId(1L)).thenReturn(classes);

        CourseDetailDTO result = courseService.getCourseDetailById(1L);

        assertEquals(detail, result);
        assertEquals(sections, result.getSections());
        assertEquals(classes, result.getClasses());
    }

    @Test
    @DisplayName("获取课程详细信息 - 课程不存在")
    void testGetCourseDetailById_notExist() {
        when(courseMapper.getCourseDetailById(2L)).thenReturn(null);

        CourseDetailDTO result = courseService.getCourseDetailById(2L);

        assertNull(result);
    }

    @Test
    @DisplayName("获取用户的课程详细信息列表 - 正常")
    void testGetCourseDetailsByUserId_success() {
        CourseDetailDTO detail = new CourseDetailDTO();
        detail.setId(1L);
        List<CourseDetailDTO> details = Arrays.asList(detail);

        List<CourseSection> sections = Arrays.asList(new CourseSection());
        List<CourseDetailDTO.ClassInfo> classes = Arrays.asList(new CourseDetailDTO.ClassInfo());

        when(courseMapper.getCourseDetailsByUserId(1L)).thenReturn(details);
        when(courseMapper.getSectionsByCourseId(1L)).thenReturn(sections);
        when(courseMapper.getClassesByCourseId(1L)).thenReturn(classes);

        List<CourseDetailDTO> result = courseService.getCourseDetailsByUserId(1L);

        assertEquals(1, result.size());
        assertEquals(sections, result.get(0).getSections());
        assertEquals(classes, result.get(0).getClasses());
    }

    @Test
    @DisplayName("获取用户的课程详细信息列表 - 用户ID为空")
    void testGetCourseDetailsByUserId_userIdNull() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> courseService.getCourseDetailsByUserId(null));
        assertEquals("用户ID不能为空", ex.getMessage());
    }

    @Test
    @DisplayName("获取所有课程的详细信息列表 - 正常")
    void testGetAllCourses_success() {
        List<CourseDetailDTO> details = Arrays.asList(new CourseDetailDTO(), new CourseDetailDTO());
        when(courseMapper.selectAllCoursesWithNames(any())).thenReturn(details);

        List<CourseDetailDTO> result = courseService.getAllCourses();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("获取所有课程的详细信息列表 - 空列表")
    void testGetAllCourses_empty() {
        when(courseMapper.selectAllCoursesWithNames(any())).thenReturn(Collections.emptyList());

        List<CourseDetailDTO> result = courseService.getAllCourses();

        assertTrue(result.isEmpty());
    }
}
