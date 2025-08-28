package org.example.edusoft.controller.course;

import org.example.edusoft.entity.course.CourseSection;
import org.example.edusoft.service.course.CourseSectionService;
import org.example.edusoft.exception.CourseSectionException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseSectionController.class)
class CourseSectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseSectionService courseSectionService;

    // createSections success
    @Test
    void createSections_success() throws Exception {
        String json = "{\"sections\":[{\"id\":1,\"sectionName\":\"第一章\"},{\"id\":2,\"sectionName\":\"第二章\"}]}";
        mockMvc.perform(post("/api/courses/10/sections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.code").value(200))
                .andExpect(jsonPath("$.data.message").value("章节创建成功"));
        Mockito.verify(courseSectionService).createSections(anyList());
    }

    // createSections CourseSectionException
    // @Test
    // void createSections_courseSectionException() throws Exception {
    //     Mockito.doThrow(new CourseSectionException(123, "自定义错误"))
    //             .when(courseSectionService).createSections(anyList());
    //     String json = "{\"sections\":[{\"id\":1,\"sectionName\":\"第一章\"}]}";
    //     mockMvc.perform(post("/api/courses/10/sections")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(json))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.code").value(500))
    //             .andExpect(jsonPath("$.msg").value("123: 自定义错误"));
    // }

    // createSections Exception
    @Test
    void createSections_generalException() throws Exception {
        Mockito.doThrow(new RuntimeException("数据库挂了"))
                .when(courseSectionService).createSections(anyList());
        String json = "{\"sections\":[{\"id\":1,\"sectionName\":\"第一章\"}]}";
        mockMvc.perform(post("/api/courses/10/sections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("系统错误：")));
    }

    // deleteSection success
    @Test
    void deleteSection_success() throws Exception {
        mockMvc.perform(delete("/api/courses/10/sections/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.code").value(200))
                .andExpect(jsonPath("$.data.message").value("章节删除成功"));
        Mockito.verify(courseSectionService).deleteSection(99L);
    }

    // deleteSection CourseSectionException
    @Test
    void deleteSection_courseSectionException() throws Exception {
        Mockito.doThrow(new CourseSectionException(456, "删除失败"))
                .when(courseSectionService).deleteSection(99L);
        mockMvc.perform(delete("/api/courses/10/sections/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("456: 删除失败"));
    }

    // deleteSection Exception
    @Test
    void deleteSection_generalException() throws Exception {
        Mockito.doThrow(new RuntimeException("未知错误")).when(courseSectionService).deleteSection(99L);
        mockMvc.perform(delete("/api/courses/10/sections/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("系统错误：")));
    }

    // getSectionsByCourseId success
    @Test
    void getSectionsByCourseId_success() throws Exception {
        List<CourseSection> mockSections = new ArrayList<>();
        CourseSection s1 = new CourseSection();
        s1.setId(1L); s1.setSectionName("A");
        CourseSection s2 = new CourseSection();
        s2.setId(2L); s2.setSectionName("B");
        mockSections.add(s1);
        mockSections.add(s2);

        Mockito.when(courseSectionService.getSectionsByCourseId(10L)).thenReturn(mockSections);

        mockMvc.perform(get("/api/courses/10/sections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].sectionName").value("A"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].sectionName").value("B"));
    }

    // getSectionsByCourseId CourseSectionException
    @Test
    void getSectionsByCourseId_courseSectionException() throws Exception {
        Mockito.when(courseSectionService.getSectionsByCourseId(10L))
                .thenThrow(new CourseSectionException(789, "查询失败"));
        mockMvc.perform(get("/api/courses/10/sections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("789: 查询失败"));
    }

    // getSectionsByCourseId Exception
    @Test
    void getSectionsByCourseId_generalException() throws Exception {
        Mockito.when(courseSectionService.getSectionsByCourseId(10L))
                .thenThrow(new RuntimeException("服务器炸了"));
        mockMvc.perform(get("/api/courses/10/sections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("系统错误：")));
    }
}