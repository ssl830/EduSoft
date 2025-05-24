package org.example.edusoft.controller.resource;

import org.example.edusoft.entity.resource.TeachingResource;
import org.example.edusoft.entity.resource.LearningProgress;
import org.example.edusoft.service.resource.TeachingResourceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeachingResourceController.class)
class TeachingResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeachingResourceService resourceService;

    @Test
    void uploadResource_Success() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.mp4",
            "video/mp4",
            "test video content".getBytes()
        );

        TeachingResource resource = createResource(1L, 1L, 1L);
        when(resourceService.uploadResource(any(), anyLong(), anyLong(), anyString(), anyString(), anyString(), anyLong()))
            .thenReturn(resource);

        // Act & Assert
        mockMvc.perform(multipart("/api/resources/upload")
                .file(file)
                .param("courseId", "1")
                .param("chapterId", "1")
                .param("chapterName", "Chapter 1")
                .param("title", "Test Video")
                .param("description", "Test Description")
                .param("createdBy", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.message").value("资源上传成功"));
    }

    @Test
    void getResource_Success() throws Exception {
        // Arrange
        TeachingResource resource = createResource(1L, 1L, 1L);
        when(resourceService.getResource(1L)).thenReturn(resource);

        // Act & Assert
        mockMvc.perform(get("/api/resources/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.message").value("获取资源成功"));
    }

    @Test
    void getResourcesByCourse_Success() throws Exception {
        // Arrange
        Map<Long, List<TeachingResource>> resourceMap = new HashMap<>();
        resourceMap.put(1L, Arrays.asList(createResource(1L, 1L, 1L), createResource(2L, 1L, 1L)));
        when(resourceService.getResourcesByCourse(1L)).thenReturn(resourceMap);

        // Act & Assert
        mockMvc.perform(get("/api/resources/list/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.message").value("获取课程资源列表成功"));
    }

    @Test
    void deleteResource_Success() throws Exception {
        // Arrange
        when(resourceService.deleteResource(1L, 1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/resources/1")
                .param("operatorId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("资源删除成功"));
    }

    @Test
    void updateProgress_Success() throws Exception {
        // Arrange
        LearningProgress progress = new LearningProgress();
        progress.setResourceId(1L);
        progress.setStudentId(1L);
        progress.setProgress(60);
        progress.setLastPosition(55);

        when(resourceService.updateProgress(1L, 1L, 60, 55)).thenReturn(progress);

        // Act & Assert
        mockMvc.perform(post("/api/resources/progress")
                .param("resourceId", "1")
                .param("studentId", "1")
                .param("progress", "60")
                .param("position", "55")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.message").value("更新学习进度成功"));
    }

    @Test
    void getProgress_Success() throws Exception {
        // Arrange
        LearningProgress progress = new LearningProgress();
        progress.setResourceId(1L);
        progress.setStudentId(1L);
        when(resourceService.getProgress(1L, 1L)).thenReturn(progress);

        // Act & Assert
        mockMvc.perform(get("/api/resources/progress/1/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.message").value("获取学习进度成功"));
    }

    @Test
    void getResourceUrl_Success() throws Exception {
        // Arrange
        String url = "https://example.com/video.mp4";
        when(resourceService.getResourceUrl(1L)).thenReturn(url);

        // Act & Assert
        mockMvc.perform(get("/api/resources/url/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").value(url))
            .andExpect(jsonPath("$.message").value("获取资源访问链接成功"));
    }

    private TeachingResource createResource(Long id, Long courseId, Long chapterId) {
        TeachingResource resource = new TeachingResource();
        resource.setId(id);
        resource.setCourseId(courseId);
        resource.setChapterId(chapterId);
        resource.setCreatedAt(LocalDateTime.now());
        return resource;
    }
} 