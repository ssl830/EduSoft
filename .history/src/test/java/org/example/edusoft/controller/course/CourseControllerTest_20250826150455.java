package org.example.edusoft.controller.course;

import org.example.edusoft.entity.course.Course;
import org.example.edusoft.entity.course.CourseDetailDTO;
import org.example.edusoft.service.course.CourseService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    // 1. createCourse
    @Test
    void createCourse_success() throws Exception {
        Course mockCourse = new Course();
        mockCourse.setId(1L);
        mockCourse.setName("Test Course");
        
        Mockito.when(courseService.createCourse(any(Course.class))).thenReturn(mockCourse);

        mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"courseName\":\"Test Course\",\"description\":\"Test Description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.courseName").value("Test Course"));
    }

    @Test
    void createCourse_fail() throws Exception {
        Mockito.when(courseService.createCourse(any(Course.class)))
                .thenThrow(new RuntimeException("创建失败"));

        mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"courseName\":\"Test Course\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("创建课程失败：创建失败"));
    }

    // 2. getCoursesByUserId
    @Test
    void getCoursesByUserId_success() throws Exception {
        CourseDetailDTO course1 = new CourseDetailDTO();
        course1.setId(1L);
        course1.setName("Course 1");
        
        CourseDetailDTO course2 = new CourseDetailDTO();
        course2.setId(2L);
        course2.setCourseName("Course 2");
        
        List<CourseDetailDTO> courses = Arrays.asList(course1, course2);
        
        Mockito.when(courseService.getCourseDetailsByUserId(1L)).thenReturn(courses);

        mockMvc.perform(get("/api/courses/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].courseName").value("Course 1"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].courseName").value("Course 2"));
    }

    @Test
    void getCoursesByUserId_fail() throws Exception {
        Mockito.when(courseService.getCourseDetailsByUserId(1L))
                .thenThrow(new RuntimeException("数据库连接失败"));

        mockMvc.perform(get("/api/courses/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("获取课程列表失败：数据库连接失败"));
    }

    // 3. getCourseById
    @Test
    void getCourseById_success() throws Exception {
        CourseDetailDTO mockCourse = new CourseDetailDTO();
        mockCourse.setId(1L);
        mockCourse.setCourseName("Test Course");
        mockCourse.setDescription("Course Description");
        
        Mockito.when(courseService.getCourseDetailById(1L)).thenReturn(mockCourse);

        mockMvc.perform(get("/api/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.courseName").value("Test Course"))
                .andExpect(jsonPath("$.data.description").value("Course Description"));
    }

    @Test
    void getCourseById_fail() throws Exception {
        Mockito.when(courseService.getCourseDetailById(1L))
                .thenThrow(new RuntimeException("课程不存在"));

        mockMvc.perform(get("/api/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("获取课程详情失败：课程不存在"));
    }

    // 4. updateCourse
    @Test
    void updateCourse_success() throws Exception {
        Course mockCourse = new Course();
        mockCourse.setId(1L);
        mockCourse.setCourseName("Updated Course");
        mockCourse.setDescription("Updated Description");
        
        Mockito.when(courseService.updateCourse(any(Course.class))).thenReturn(mockCourse);

        mockMvc.perform(put("/api/courses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"courseName\":\"Updated Course\",\"description\":\"Updated Description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.courseName").value("Updated Course"))
                .andExpect(jsonPath("$.data.description").value("Updated Description"));
    }

    @Test
    void updateCourse_fail() throws Exception {
        Mockito.when(courseService.updateCourse(any(Course.class)))
                .thenThrow(new RuntimeException("更新权限不足"));

        mockMvc.perform(put("/api/courses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"courseName\":\"Updated Course\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("更新课程失败：更新权限不足"));
    }

    // 5. deleteCourse
    @Test
    void deleteCourse_success() throws Exception {
        Mockito.when(courseService.deleteCourse(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void deleteCourse_fail() throws Exception {
        Mockito.when(courseService.deleteCourse(1L))
                .thenThrow(new RuntimeException("无法删除，课程下还有班级"));

        mockMvc.perform(delete("/api/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("删除课程失败：无法删除，课程下还有班级"));
    }

    // 6. getAllCourses
    @Test
    void getAllCourses_success() throws Exception {
        CourseDetailDTO course1 = new CourseDetailDTO();
        course1.setId(1L);
        course1.setCourseName("Math");
        
        CourseDetailDTO course2 = new CourseDetailDTO();
        course2.setId(2L);
        course2.setCourseName("Physics");
        
        CourseDetailDTO course3 = new CourseDetailDTO();
        course3.setId(3L);
        course3.setCourseName("Chemistry");
        
        List<CourseDetailDTO> allCourses = Arrays.asList(course1, course2, course3);
        
        Mockito.when(courseService.getAllCourses()).thenReturn(allCourses);

        mockMvc.perform(get("/api/courses/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0].courseName").value("Math"))
                .andExpect(jsonPath("$.data[1].courseName").value("Physics"))
                .andExpect(jsonPath("$.data[2].courseName").value("Chemistry"));
    }

    @Test
    void getAllCourses_fail() throws Exception {
        Mockito.when(courseService.getAllCourses())
                .thenThrow(new RuntimeException("系统维护中"));

        mockMvc.perform(get("/api/courses/list"))
                .andExpected(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("获取课程列表失败：系统维护中"));
    }
}