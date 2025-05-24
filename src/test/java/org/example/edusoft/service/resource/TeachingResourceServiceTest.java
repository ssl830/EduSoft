package org.example.edusoft.service.resource;

import org.example.edusoft.entity.resource.TeachingResource;
import org.example.edusoft.entity.resource.LearningProgress;
import org.example.edusoft.mapper.resource.TeachingResourceMapper;
import org.example.edusoft.mapper.resource.LearningProgressMapper;
import org.example.edusoft.service.resource.impl.TeachingResourceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TeachingResourceServiceTest {

    @Mock
    private TeachingResourceMapper resourceMapper;

    @Mock
    private LearningProgressMapper progressMapper;

    @Mock
    private OSS ossClient;

    @InjectMocks
    private TeachingResourceServiceImpl resourceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadResource_Success() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "test.mp4",
            "test.mp4",
            "video/mp4",
            "test video content".getBytes()
        );

        TeachingResource expectedResource = new TeachingResource();
        expectedResource.setId(1L);
        expectedResource.setTitle("Test Video");
        expectedResource.setCourseId(1L);
        expectedResource.setChapterId(1L);

        // Use PutObjectRequest to avoid ambiguity
        doNothing().when(ossClient).putObject(any(PutObjectRequest.class));
        doNothing().when(resourceMapper).insert(any(TeachingResource.class));

        // Act
        TeachingResource result = resourceService.uploadResource(
            file, 1L, 1L, "Chapter 1", "Test Video", "Test Description", 1L);

        // Assert
        assertNotNull(result);
        verify(ossClient).putObject(any(PutObjectRequest.class));
        verify(resourceMapper).insert(any(TeachingResource.class));
    }

    @Test
    void getResource_Success() {
        // Arrange
        TeachingResource expectedResource = new TeachingResource();
        expectedResource.setId(1L);
        when(resourceMapper.selectById(1L)).thenReturn(expectedResource);

        // Act
        TeachingResource result = resourceService.getResource(1L);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResource.getId(), result.getId());
        verify(resourceMapper).selectById(1L);
    }

    @Test
    void getResourcesByCourse_Success() {
        // Arrange
        List<TeachingResource> resources = Arrays.asList(
            createResource(1L, 1L, 1L),
            createResource(2L, 1L, 1L),
            createResource(3L, 1L, 2L)
        );
        when(resourceMapper.selectByCourseId(1L)).thenReturn(resources);

        // Act
        Map<Long, List<TeachingResource>> result = resourceService.getResourcesByCourse(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey(1L));
        assertTrue(result.containsKey(2L));
        verify(resourceMapper).selectByCourseId(1L);
    }

    @Test
    void deleteResource_Success() {
        // Arrange
        TeachingResource resource = createResource(1L, 1L, 1L);
        when(resourceMapper.selectById(1L)).thenReturn(resource);
        when(resourceMapper.deleteById(1L)).thenReturn(1);
        doNothing().when(ossClient).deleteObject(anyString(), anyString());

        // Act
        boolean result = resourceService.deleteResource(1L, 1L);

        // Assert
        assertTrue(result);
        verify(ossClient).deleteObject(anyString(), anyString());
        verify(resourceMapper).deleteById(1L);
    }

    @Test
    void updateProgress_Success() {
        // Arrange
        LearningProgress progress = new LearningProgress();
        progress.setResourceId(1L);
        progress.setStudentId(1L);
        progress.setProgress(60);
        progress.setLastPosition(55);

        when(progressMapper.selectByResourceAndStudent(1L, 1L)).thenReturn(progress);

        // Act
        LearningProgress result = resourceService.updateProgress(1L, 1L, 60, 55);

        // Assert
        assertNotNull(result);
        assertEquals(60, result.getProgress());
        assertEquals(55, result.getLastPosition());
        verify(progressMapper).insertOrUpdate(any(LearningProgress.class));
        verify(progressMapper).selectByResourceAndStudent(1L, 1L);
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