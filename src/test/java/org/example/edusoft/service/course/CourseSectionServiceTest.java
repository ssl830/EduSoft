package org.example.edusoft.service.course;

import org.example.edusoft.entity.course.CourseSection;
import org.example.edusoft.exception.CourseSectionException;
import org.example.edusoft.mapper.course.CourseSectionMapper;
import org.example.edusoft.service.course.impl.CourseSectionServiceImpl;
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
class CourseSectionServiceTest {

    @Mock
    private CourseSectionMapper courseSectionMapper;

    @InjectMocks
    private CourseSectionServiceImpl courseSectionService;

    @Test
    @DisplayName("创建章节 - 正常创建")
    void testCreateSections_success() {
        CourseSection section = new CourseSection();
        section.setCourseId(1L);
        section.setTitle("第一章");
        section.setSortOrder(1);

        when(courseSectionMapper.getSectionsByCourseId(1L)).thenReturn(Collections.emptyList());
        when(courseSectionMapper.insert(any(CourseSection.class))).thenReturn(1);

        assertDoesNotThrow(() -> courseSectionService.createSections(List.of(section)));
        verify(courseSectionMapper, times(1)).insert(any(CourseSection.class));
    }

    @Test
    @DisplayName("创建章节 - sections为空")
    void testCreateSections_emptyList() {
        CourseSectionException ex = assertThrows(CourseSectionException.class, () -> courseSectionService.createSections(Collections.emptyList()));
        assertEquals("SECTION_001", ex.getCode());
    }

    @Test
    @DisplayName("创建章节 - 课程ID为空")
    void testCreateSections_courseIdNull() {
        CourseSection section = new CourseSection();
        section.setTitle("第一章");
        section.setSortOrder(1);

        CourseSectionException ex = assertThrows(CourseSectionException.class, () -> courseSectionService.createSections(List.of(section)));
        assertEquals("SECTION_002", ex.getCode());
    }

    @Test
    @DisplayName("创建章节 - 标题为空")
    void testCreateSections_titleEmpty() {
        CourseSection section = new CourseSection();
        section.setCourseId(1L);
        section.setTitle("");
        section.setSortOrder(1);

        CourseSectionException ex = assertThrows(CourseSectionException.class, () -> courseSectionService.createSections(List.of(section)));
        assertEquals("SECTION_003", ex.getCode());
    }

    @Test
    @DisplayName("获取章节列表 - 正常获取")
    void testGetSectionsByCourseId_success() {
        List<CourseSection> sections = Arrays.asList(new CourseSection(), new CourseSection());
        when(courseSectionMapper.getSectionsByCourseId(1L)).thenReturn(sections);

        List<CourseSection> result = courseSectionService.getSectionsByCourseId(1L);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("获取章节列表 - 课程ID为空")
    void testGetSectionsByCourseId_courseIdNull() {
        CourseSectionException ex = assertThrows(CourseSectionException.class, () -> courseSectionService.getSectionsByCourseId(null));
        assertEquals("SECTION_005", ex.getCode());
    }

    @Test
    @DisplayName("获取章节列表 - Mapper异常")
    void testGetSectionsByCourseId_mapperException() {
        when(courseSectionMapper.getSectionsByCourseId(1L)).thenThrow(new RuntimeException("数据库异常"));

        CourseSectionException ex = assertThrows(CourseSectionException.class, () -> courseSectionService.getSectionsByCourseId(1L));
        assertEquals("SECTION_006", ex.getCode());
    }

    @Test
    @DisplayName("更新章节 - 正常更新")
    void testUpdateSection_success() {
        CourseSection section = new CourseSection();
        section.setId(1L);
        section.setTitle("新标题");
        when(courseSectionMapper.selectById(1L)).thenReturn(section);
        when(courseSectionMapper.updateById(section)).thenReturn(1);

        CourseSection result = courseSectionService.updateSection(section);

        assertEquals(section, result);
    }

    @Test
    @DisplayName("更新章节 - section为null")
    void testUpdateSection_sectionNull() {
        CourseSectionException ex = assertThrows(CourseSectionException.class, () -> courseSectionService.updateSection(null));
        assertEquals("SECTION_007", ex.getCode());
    }

    @Test
    @DisplayName("更新章节 - 章节ID为空")
    void testUpdateSection_idNull() {
        CourseSection section = new CourseSection();
        section.setTitle("新标题");

        CourseSectionException ex = assertThrows(CourseSectionException.class, () -> courseSectionService.updateSection(section));
        assertEquals("SECTION_008", ex.getCode());
    }

    @Test
    @DisplayName("更新章节 - 标题为空")
    void testUpdateSection_titleEmpty() {
        CourseSection section = new CourseSection();
        section.setId(1L);
        section.setTitle("");

        CourseSectionException ex = assertThrows(CourseSectionException.class, () -> courseSectionService.updateSection(section));
        assertEquals("SECTION_009", ex.getCode());
    }

    @Test
    @DisplayName("更新章节 - 章节不存在")
    void testUpdateSection_notExist() {
        CourseSection section = new CourseSection();
        section.setId(1L);
        section.setTitle("新标题");
        when(courseSectionMapper.selectById(1L)).thenReturn(null);

        CourseSectionException ex = assertThrows(CourseSectionException.class, () -> courseSectionService.updateSection(section));
        assertEquals("SECTION_010", ex.getCode());
    }

    @Test
    @DisplayName("更新章节 - Mapper异常")
    void testUpdateSection_mapperException() {
        CourseSection section = new CourseSection();
        section.setId(1L);
        section.setTitle("新标题");
        when(courseSectionMapper.selectById(1L)).thenReturn(section);
        when(courseSectionMapper.updateById(section)).thenThrow(new RuntimeException("数据库异常"));

        CourseSectionException ex = assertThrows(CourseSectionException.class, () -> courseSectionService.updateSection(section));
        assertEquals("SECTION_011", ex.getCode());
    }

    @Test
    @DisplayName("删除章节 - 正常删除")
    void testDeleteSection_success() {
        CourseSection section = new CourseSection();
        section.setId(1L);
        when(courseSectionMapper.selectById(1L)).thenReturn(section);
        when(courseSectionMapper.deleteById(1L)).thenReturn(1);

        boolean result = courseSectionService.deleteSection(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("删除章节 - 章节ID为空")
    void testDeleteSection_idNull() {
        CourseSectionException ex = assertThrows(CourseSectionException.class, () -> courseSectionService.deleteSection(null));
        assertEquals("SECTION_012", ex.getCode());
    }

    @Test
    @DisplayName("删除章节 - 章节不存在")
    void testDeleteSection_notExist() {
        when(courseSectionMapper.selectById(1L)).thenReturn(null);

        CourseSectionException ex = assertThrows(CourseSectionException.class, () -> courseSectionService.deleteSection(1L));
        assertEquals("SECTION_013", ex.getCode());
    }

    @Test
    @DisplayName("删除章节 - Mapper异常")
    void testDeleteSection_mapperException() {
        CourseSection section = new CourseSection();
        section.setId(1L);
        when(courseSectionMapper.selectById(1L)).thenReturn(section);
        when(courseSectionMapper.deleteById(1L)).thenThrow(new RuntimeException("数据库异常"));

        CourseSectionException ex = assertThrows(CourseSectionException.class, () -> courseSectionService.deleteSection(1L));
        assertEquals("SECTION_014", ex.getCode());
    }
}

