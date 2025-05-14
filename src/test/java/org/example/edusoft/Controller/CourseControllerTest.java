package org.example.edusoft.Controller;

import org.example.edusoft.entity.Course;
import org.example.edusoft.entity.Resource;
import org.example.edusoft.entity.CourseSection;
import org.example.edusoft.service.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateCourse() throws Exception {
        Course course = new Course();
        course.setName("测试课程");
        course.setCode("TEST101");
        course.setTeacherId(1L);
        course.setOutline("课程大纲");
        course.setObjective("教学目标");
        course.setAssessment("考核方式");

        when(courseService.createCourse(any(Course.class))).thenReturn(course);

        mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(course)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("测试课程"))
                .andExpect(jsonPath("$.code").value("TEST101"));
    }

    @Test
    public void testUpdateCourse() throws Exception {
        Course course = new Course();
        course.setId(1L);
        course.setName("更新后的课程");
        course.setCode("TEST101");

        when(courseService.updateCourse(any(Course.class))).thenReturn(course);

        mockMvc.perform(put("/api/courses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(course)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("更新后的课程"));
    }

    @Test
    public void testGetCourse() throws Exception {
        Course course = new Course();
        course.setId(1L);
        course.setName("测试课程");

        when(courseService.getCourse(1L)).thenReturn(course);

        mockMvc.perform(get("/api/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("测试课程"));
    }

    @Test
    public void testGetTeacherCourses() throws Exception {
        Course course1 = new Course();
        course1.setId(1L);
        course1.setName("课程1");
        Course course2 = new Course();
        course2.setId(2L);
        course2.setName("课程2");

        List<Course> courses = Arrays.asList(course1, course2);

        when(courseService.getTeacherCourses(1L)).thenReturn(courses);

        mockMvc.perform(get("/api/courses/teacher/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("课程1"))
                .andExpect(jsonPath("$[1].name").value("课程2"));
    }

    @Test
    public void testAddSection() throws Exception {
        CourseSection section = new CourseSection();
        section.setTitle("测试章节");
        section.setCourseId(1L);

        when(courseService.addSection(any(CourseSection.class))).thenReturn(section);

        mockMvc.perform(post("/api/courses/1/sections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(section)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("测试章节"));
    }

    @Test
    public void testDeleteSection() throws Exception {
        mockMvc.perform(delete("/api/courses/1/sections/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUploadResource() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pptx",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                "test content".getBytes()
        );

        Resource resource = new Resource();
        resource.setTitle("测试资源");
        resource.setType(Resource.ResourceType.PPT);

        when(courseService.uploadResource(any(), any(), any(), any(), any())).thenReturn(resource);

        mockMvc.perform(multipart("/api/courses/1/resources")
                .file(file)
                .param("title", "测试资源")
                .param("type", "PPT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("测试资源"));
    }

    @Test
    public void testPreviewCourse() throws Exception {
        Course course = new Course();
        course.setId(1L);
        course.setName("测试课程");
        CourseSection section = new CourseSection();
        section.setTitle("测试章节");
        course.setSections(Arrays.asList(section));

        when(courseService.getCourseWithSections(1L)).thenReturn(course);

        mockMvc.perform(get("/api/courses/1/preview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("测试课程"))
                .andExpect(jsonPath("$.sections[0].title").value("测试章节"));
    }
} 