package org.example.edusoft.service.resource;

import org.example.edusoft.entity.resource.TeachingResource;
import org.example.edusoft.entity.resource.LearningProgress;
import org.example.edusoft.entity.resource.ResourceProgressDTO;
import org.example.edusoft.mapper.resource.TeachingResourceMapper;
import org.example.edusoft.mapper.resource.LearningProgressMapper;
import org.example.edusoft.common.storage.IFileStorage;
import org.example.edusoft.common.storage.IFileStorageProvider;
import org.example.edusoft.common.domain.FileBo;
import org.example.edusoft.entity.file.FileType;
import org.example.edusoft.common.properties.FsServerProperties;
import org.example.edusoft.ai.AIServiceClient;
import org.example.edusoft.service.resource.VideoSummaryService;
import org.example.edusoft.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeachingResourceServiceTest {

    @InjectMocks
    private org.example.edusoft.service.resource.impl.TeachingResourceServiceImpl teachingResourceService;

    @Mock
    private TeachingResourceMapper resourceMapper;
    @Mock
    private LearningProgressMapper progressMapper;
    @Mock
    private IFileStorage fileStorage;
    @Mock
    private IFileStorageProvider storageProvider;
    @Mock
    private FsServerProperties fsServerProperties;
    @Mock
    private AIServiceClient aiServiceClient;
    @Mock
    private VideoSummaryService videoSummaryService;

    @Mock
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        // 移除不必要的lenient桩，改为在需要的方法中单独设置
    }

    // uploadResource 正例
    @Test
    void testUploadResource_success() {
        when(storageProvider.getStorage()).thenReturn(fileStorage);
        when(multipartFile.getOriginalFilename()).thenReturn("video.mp4");
        FileBo fileBo = new FileBo();
        fileBo.setUrl("http://test.com/video.mp4");
        fileBo.setFileName("video.mp4");
        when(fileStorage.upload(any(), any(), eq(FileType.VIDEO))).thenReturn(fileBo);

        // 修改这里：对非void方法使用thenReturn而不是doNothing
        doAnswer(invocation -> {
            TeachingResource resource = invocation.getArgument(0);
            resource.setId(1L); // 模拟设置生成的ID
            return null;
        }).when(resourceMapper).insert(any(TeachingResource.class));

        TeachingResource resource = teachingResourceService.uploadResource(
                multipartFile, 1L, 2L, "章节", "标题", "描述", 3L);

        assertEquals("标题", resource.getTitle());
        assertEquals("http://test.com/video.mp4", resource.getFileUrl());
    }

    // uploadResource 反例：上传文件异常
    @Test
    void testUploadResource_fail() {
        when(storageProvider.getStorage()).thenReturn(fileStorage);
        when(multipartFile.getOriginalFilename()).thenReturn("video.mp4");
        when(fileStorage.upload(any(), any(), eq(FileType.VIDEO))).thenThrow(new RuntimeException("上传失败"));

        assertThrows(RuntimeException.class, () -> teachingResourceService.uploadResource(
                multipartFile, 1L, 2L, "章节", "标题", "描述", 3L));
    }

    // getResource 正例
    @Test
    void testGetResource_success() {
        TeachingResource resource = new TeachingResource();
        resource.setId(1L);
        when(resourceMapper.selectById(1L)).thenReturn(resource);

        TeachingResource result = teachingResourceService.getResource(1L);
        assertEquals(1L, result.getId());
    }

    // getResource 反例：资源不存在
    @Test
    void testGetResource_notFound() {
        when(resourceMapper.selectById(2L)).thenReturn(null);
        TeachingResource result = teachingResourceService.getResource(2L);
        assertNull(result);
    }

    // getResourcesByCourse 正例
    @Test
    void testGetResourcesByCourse_success() {
        TeachingResource r1 = new TeachingResource();
        r1.setChapterId(1L);
        TeachingResource r2 = new TeachingResource();
        r2.setChapterId(2L);
        when(resourceMapper.selectByCourseId(1L)).thenReturn(Arrays.asList(r1, r2));

        Map<Long, List<TeachingResource>> result = teachingResourceService.getResourcesByCourse(1L);
        assertEquals(2, result.size());
    }

    // getResourcesByCourse 反例：无资源
    @Test
    void testGetResourcesByCourse_empty() {
        when(resourceMapper.selectByCourseId(2L)).thenReturn(Collections.emptyList());
        Map<Long, List<TeachingResource>> result = teachingResourceService.getResourcesByCourse(2L);
        assertTrue(result.isEmpty());
    }

    // getResourcesByChapter 正例
    @Test
    void testGetResourcesByChapter_success() {
        TeachingResource r = new TeachingResource();
        when(resourceMapper.selectByChapter(1L, 1L)).thenReturn(Arrays.asList(r));
        List<TeachingResource> result = teachingResourceService.getResourcesByChapter(1L, 1L);
        assertEquals(1, result.size());
    }

    // getResourcesByChapter 反例：无资源
    @Test
    void testGetResourcesByChapter_empty() {
        when(resourceMapper.selectByChapter(1L, 2L)).thenReturn(Collections.emptyList());
        List<TeachingResource> result = teachingResourceService.getResourcesByChapter(1L, 2L);
        assertTrue(result.isEmpty());
    }

    // deleteResource 正例
    @Test
    void testDeleteResource_success() {
        when(storageProvider.getStorage()).thenReturn(fileStorage);
        TeachingResource r = new TeachingResource();
        r.setObjectName("obj");
        when(resourceMapper.selectById(1L)).thenReturn(r);
        doNothing().when(fileStorage).delete("obj");
        when(progressMapper.deleteByResourceId(1L)).thenReturn(1);
        when(resourceMapper.deleteById(1L)).thenReturn(1);

        boolean result = teachingResourceService.deleteResource(1L, 2L);
        assertTrue(result);
    }

    // deleteResource 反例：资源不存在
    @Test
    void testDeleteResource_notFound() {
        when(resourceMapper.selectById(2L)).thenReturn(null);
        boolean result = teachingResourceService.deleteResource(2L, 2L);
        assertFalse(result);
    }

    // updateProgress 正例
    @Test
    void testUpdateProgress_success() {
        TeachingResource r = new TeachingResource();
        r.setId(1L);
        when(resourceMapper.selectById(1L)).thenReturn(r);
        doNothing().when(progressMapper).insertOrUpdate(any());
        LearningProgress lp = new LearningProgress();
        lp.setResourceId(1L);
        lp.setStudentId(2L);
        when(progressMapper.selectByResourceAndStudent(1L, 2L)).thenReturn(lp);

        LearningProgress result = teachingResourceService.updateProgress(1L, 2L, 100, 50);
        assertEquals(1L, result.getResourceId());
        assertEquals(2L, result.getStudentId());
    }

    // updateProgress 反例：资源不存在
    @Test
    void testUpdateProgress_resourceNotFound() {
        when(resourceMapper.selectById(2L)).thenReturn(null);
        assertThrows(BusinessException.class, () -> teachingResourceService.updateProgress(2L, 2L, 100, 50));
    }

    // getProgress 正例
    @Test
    void testGetProgress_success() {
        LearningProgress lp = new LearningProgress();
        lp.setResourceId(1L);
        when(progressMapper.selectByResourceAndStudent(1L, 2L)).thenReturn(lp);
        LearningProgress result = teachingResourceService.getProgress(1L, 2L);
        assertEquals(1L, result.getResourceId());
    }

    // getProgress 反例：无进度
    @Test
    void testGetProgress_notFound() {
        when(progressMapper.selectByResourceAndStudent(2L, 2L)).thenReturn(null);
        LearningProgress result = teachingResourceService.getProgress(2L, 2L);
        assertNull(result);
    }

    // getResourceUrl 正例
    @Test
    void testGetResourceUrl_success() {
//        when(storageProvider.getStorage()).thenReturn(fileStorage);
        TeachingResource r = new TeachingResource();
        r.setObjectName("obj");
        when(resourceMapper.selectById(1L)).thenReturn(r);
        when(fileStorage.getUrl("obj")).thenReturn("http://test.com/obj");
        String url = teachingResourceService.getResourceUrl(1L);
        assertEquals("http://test.com/obj", url);
    }

    // getResourceUrl 反例：资源不存在
    @Test
    void testGetResourceUrl_notFound() {
        when(resourceMapper.selectById(2L)).thenReturn(null);
        String url = teachingResourceService.getResourceUrl(2L);
        assertNull(url);
    }

    // getSignedResourceUrl 正例
    @Test
    void testGetSignedResourceUrl_success() {
        TeachingResource r = new TeachingResource();
        r.setObjectName("obj");
        when(resourceMapper.selectById(1L)).thenReturn(r);

        FsServerProperties.AliyunOssProperties ossProps = mock(FsServerProperties.AliyunOssProperties.class);
        when(fsServerProperties.getAliyunOss()).thenReturn(ossProps);
        when(ossProps.getEndpoint()).thenReturn("endpoint");
        when(ossProps.getAccessKey()).thenReturn("ak");
        when(ossProps.getSecretKey()).thenReturn("sk");
        when(ossProps.getBucket()).thenReturn("bucket");

        // 这里直接断言不会抛异常即可
        assertDoesNotThrow(() -> teachingResourceService.getSignedResourceUrl(1L));
    }

    // getSignedResourceUrl 反例：OSS配置缺失
    @Test
    void testGetSignedResourceUrl_configMissing() {
        TeachingResource r = new TeachingResource();
        r.setObjectName("obj");
        when(resourceMapper.selectById(1L)).thenReturn(r);
        when(fsServerProperties.getAliyunOss()).thenReturn(null);

        assertThrows(BusinessException.class, () -> teachingResourceService.getSignedResourceUrl(1L));
    }
}