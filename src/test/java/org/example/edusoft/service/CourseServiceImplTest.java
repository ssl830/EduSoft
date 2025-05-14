package org.example.edusoft.service;

import org.example.edusoft.entity.Course;
import org.example.edusoft.entity.Resource;
import org.example.edusoft.entity.CourseSection;
import org.example.edusoft.mapper.CourseMapper;
import org.example.edusoft.mapper.ResourceMapper;
import org.example.edusoft.mapper.CourseSectionMapper;
import org.example.edusoft.service.impl.CourseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CourseServiceImplTest {

    @Mock
    private CourseMapper courseMapper;

    @Mock
    private ResourceMapper resourceMapper;

    @Mock
    private CourseSectionMapper courseSectionMapper;

    @InjectMocks
    private CourseServiceImpl courseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCourse() {
        Course course = new Course();
        course.setName("测试课程");
        course.setCode("TEST101");
        course.setTeacherId(1L);

        when(courseMapper.insert(any(Course.class))).thenReturn(1);

        Course result = courseService.createCourse(course);

        assertNotNull(result);
        assertEquals("测试课程", result.getName());
        assertEquals("TEST101", result.getCode());
        verify(courseMapper).insert(any(Course.class));
    }

    @Test
    void testUpdateCourse() {
        Course course = new Course();
        course.setId(1L);
        course.setName("更新后的课程");

        when(courseMapper.update(any(Course.class))).thenReturn(1);

        Course result = courseService.updateCourse(course);

        assertNotNull(result);
        assertEquals("更新后的课程", result.getName());
        verify(courseMapper).update(any(Course.class));
    }

    @Test
    void testGetCourse() {
        Course course = new Course();
        course.setId(1L);
        course.setName("测试课程");

        when(courseMapper.findById(1L)).thenReturn(course);

        Course result = courseService.getCourse(1L);

        assertNotNull(result);
        assertEquals("测试课程", result.getName());
        verify(courseMapper).findById(1L);
    }

    @Test
    void testGetTeacherCourses() {
        Course course1 = new Course();
        course1.setId(1L);
        course1.setName("课程1");
        Course course2 = new Course();
        course2.setId(2L);
        course2.setName("课程2");

        List<Course> courses = Arrays.asList(course1, course2);

        when(courseMapper.findByTeacherId(1L)).thenReturn(courses);

        List<Course> result = courseService.getTeacherCourses(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("课程1", result.get(0).getName());
        assertEquals("课程2", result.get(1).getName());
        verify(courseMapper).findByTeacherId(1L);
    }

    @Test
    void testAddSection() {
        CourseSection section = new CourseSection();
        section.setCourseId(1L);
        section.setTitle("测试章节");

        when(courseSectionMapper.findMaxSortOrderByCourseId(1L)).thenReturn(1);
        when(courseSectionMapper.insert(any(CourseSection.class))).thenReturn(1);

        CourseSection result = courseService.addSection(section);

        assertNotNull(result);
        assertEquals("测试章节", result.getTitle());
        assertEquals(2, result.getSortOrder());
        verify(courseSectionMapper).insert(any(CourseSection.class));
    }

    @Test
    void testDeleteSection() {
        when(courseSectionMapper.deleteById(1L)).thenReturn(1);

        courseService.deleteSection(1L);

        verify(courseSectionMapper).deleteById(1L);
    }

    @Test
    void testUploadResource() {
        MultipartFile file = new MockMultipartFile(
                "test.pptx",
                "test.pptx",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "test content".getBytes()
        );

        when(resourceMapper.insert(any(Resource.class))).thenReturn(1);

        Resource result = courseService.uploadResource(1L, 1L, file, "测试资源", Resource.ResourceType.PPT);

        assertNotNull(result);
        assertEquals("测试资源", result.getTitle());
        assertEquals(Resource.ResourceType.PPT, result.getType());
        assertEquals(Resource.Visibility.PUBLIC, result.getVisibility());
        verify(resourceMapper).insert(any(Resource.class));
    }

    @Test
    void testGetCourseWithSections() {
        Course course = new Course();
        course.setId(1L);
        course.setName("测试课程");

        CourseSection section = new CourseSection();
        section.setId(1L);
        section.setTitle("测试章节");

        when(courseMapper.findById(1L)).thenReturn(course);
        when(courseSectionMapper.findByCourseId(1L)).thenReturn(Arrays.asList(section));

        Course result = courseService.getCourseWithSections(1L);

        assertNotNull(result);
        assertEquals("测试课程", result.getName());
        assertNotNull(result.getSections());
        assertEquals(1, result.getSections().size());
        assertEquals("测试章节", result.getSections().get(0).getTitle());
        verify(courseMapper).findById(1L);
        verify(courseSectionMapper).findByCourseId(1L);
    }
} 