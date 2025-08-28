package org.example.edusoft.controller.resource;

import org.example.edusoft.common.domain.Result;
import org.example.edusoft.entity.resource.TeachingResource;
import org.example.edusoft.entity.resource.LearningProgress;
import org.example.edusoft.entity.resource.ResourceProgressDTO;
import org.example.edusoft.entity.resource.ChapterResourceRequest;
import org.example.edusoft.common.exception.BusinessException;
import org.example.edusoft.service.resource.TeachingResourceService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeachingResourceController.class)
class TeachingResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeachingResourceService resourceService;

    // uploadResource success
    @Test
    void uploadResource_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", new byte[1024]);
        TeachingResource resource = new TeachingResource();
        resource.setId(1L);
        Mockito.when(resourceService.uploadResource(any(), anyLong(), anyLong(), anyString(), anyString(), anyString(), anyLong()))
                .thenReturn(resource);

        mockMvc.perform(multipart("/api/resources/upload")
                .file(file)
                .param("courseId", "1")
                .param("chapterId", "2")
                .param("chapterName", "ch")
                .param("title", "t")
                .param("description", "desc")
                .param("createdBy", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("资源上传成功"))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void uploadResource_fail_emptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", new byte[0]);
        mockMvc.perform(multipart("/api/resources/upload")
                .file(file)
                .param("courseId", "1")
                .param("chapterId", "2")
                .param("chapterName", "ch")
                .param("title", "t")
                .param("description", "desc")
                .param("createdBy", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("上传文件不能为空"));
    }

    @Test
    void uploadResource_fail_tooLarge() throws Exception {
        byte[] big = new byte[500 * 1024 * 1024 + 1];
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", big);
        mockMvc.perform(multipart("/api/resources/upload")
                .file(file)
                .param("courseId", "1")
                .param("chapterId", "2")
                .param("chapterName", "ch")
                .param("title", "t")
                .param("description", "desc")
                .param("createdBy", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("文件大小不能超过500MB"));
    }

    @Test
    void uploadResource_fail_notVideo() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", new byte[100]);
        mockMvc.perform(multipart("/api/resources/upload")
                .file(file)
                .param("courseId", "1")
                .param("chapterId", "2")
                .param("chapterName", "ch")
                .param("title", "t")
                .param("description", "desc")
                .param("createdBy", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("请上传视频文件"));
    }

    @Test
    void uploadResource_fail_exception() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", new byte[1024]);
        Mockito.when(resourceService.uploadResource(any(), anyLong(), anyLong(), anyString(), anyString(), anyString(), anyLong()))
                .thenThrow(new RuntimeException("fail!"));

        mockMvc.perform(multipart("/api/resources/upload")
                .file(file)
                .param("courseId", "1")
                .param("chapterId", "2")
                .param("chapterName", "ch")
                .param("title", "t")
                .param("description", "desc")
                .param("createdBy", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("资源上传失败：")));
    }

    // getResource success/fail
    @Test
    void getResource_success() throws Exception {
        TeachingResource resource = new TeachingResource();
        resource.setId(1L);
        Mockito.when(resourceService.getResource(1L)).thenReturn(resource);

        mockMvc.perform(get("/api/resources/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.msg").value("获取资源成功"));
    }

    @Test
    void getResource_notFound() throws Exception {
        Mockito.when(resourceService.getResource(1L)).thenReturn(null);
        mockMvc.perform(get("/api/resources/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("资源不存在"));
    }

    @Test
    void getResource_fail() throws Exception {
        Mockito.when(resourceService.getResource(1L)).thenThrow(new RuntimeException("fail!"));
        mockMvc.perform(get("/api/resources/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("获取资源失败：")));
    }

    // getResourcesByCourse success/fail
    @Test
    void getResourcesByCourse_success() throws Exception {
        Map<Long, List<TeachingResource>> map = new HashMap<>();
        map.put(2L, Arrays.asList(new TeachingResource()));
        Mockito.when(resourceService.getResourcesByCourse(1L)).thenReturn(map);

        mockMvc.perform(get("/api/resources/list/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("获取课程资源列表成功"))
                .andExpect(jsonPath("$.data.2[0]").exists());
    }

    @Test
    void getResourcesByCourse_fail() throws Exception {
        Mockito.when(resourceService.getResourcesByCourse(1L)).thenThrow(new RuntimeException("fail!"));
        mockMvc.perform(get("/api/resources/list/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("获取课程资源列表失败：")));
    }

    // getResourcesByChapter success/fail
    @Test
    void getResourcesByChapter_success() throws Exception {
        List<TeachingResource> list = Arrays.asList(new TeachingResource(), new TeachingResource());
        Mockito.when(resourceService.getResourcesByChapter(1L, 2L)).thenReturn(list);

        mockMvc.perform(get("/api/resources/chapter/1/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("获取章节资源列表成功"))
                .andExpect(jsonPath("$.data[0]").exists());
    }
    @Test
    void getResourcesByChapter_fail() throws Exception {
        Mockito.when(resourceService.getResourcesByChapter(1L, 2L)).thenThrow(new RuntimeException("fail!"));
        mockMvc.perform(get("/api/resources/chapter/1/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("获取章节资源列表失败：")));
    }

    // deleteResource success/not found/fail
    @Test
    void deleteResource_success() throws Exception {
        Mockito.when(resourceService.deleteResource(1L, 10L)).thenReturn(true);
        mockMvc.perform(delete("/api/resources/1?operatorId=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("资源删除成功"));
    }
    @Test
    void deleteResource_notFound() throws Exception {
        Mockito.when(resourceService.deleteResource(1L, 10L)).thenReturn(false);
        mockMvc.perform(delete("/api/resources/1?operatorId=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("资源不存在或删除失败"));
    }
    @Test
    void deleteResource_fail() throws Exception {
        Mockito.when(resourceService.deleteResource(1L, 10L)).thenThrow(new RuntimeException("fail!"));
        mockMvc.perform(delete("/api/resources/1?operatorId=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("删除资源失败：")));
    }

    // updateProgress success/resource not found/fail
    @Test
    void updateProgress_success() throws Exception {
        TeachingResource r = new TeachingResource();
        r.setId(2L);
        LearningProgress lp = new LearningProgress();
        Mockito.when(resourceService.getResource(2L)).thenReturn(r);
        Mockito.when(resourceService.updateProgress(2L, 3L, 100, 200)).thenReturn(lp);

        String json = "{\"resourceId\":2,\"studentId\":3,\"progress\":100,\"position\":200}";
        mockMvc.perform(post("/api/resources/progress")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("更新学习进度成功"))
                .andExpect(jsonPath("$.data").exists());
    }
    @Test
    void updateProgress_notFound() throws Exception {
        Mockito.when(resourceService.getResource(2L)).thenReturn(null);
        String json = "{\"resourceId\":2,\"studentId\":3,\"progress\":100,\"position\":200}";
        mockMvc.perform(post("/api/resources/progress")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("教学资源不存在，ID: 2"));
    }
    @Test
    void updateProgress_businessException() throws Exception {
        TeachingResource r = new TeachingResource();
        r.setId(2L);
        Mockito.when(resourceService.getResource(2L)).thenReturn(r);
        Mockito.when(resourceService.updateProgress(anyLong(), anyLong(), anyInt(), anyInt()))
                .thenThrow(new BusinessException("业务异常"));
        String json = "{\"resourceId\":2,\"studentId\":3,\"progress\":100,\"position\":200}";
        mockMvc.perform(post("/api/resources/progress")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("业务异常"));
    }
    @Test
    void updateProgress_fail() throws Exception {
        TeachingResource r = new TeachingResource();
        r.setId(2L);
        Mockito.when(resourceService.getResource(2L)).thenReturn(r);
        Mockito.when(resourceService.updateProgress(anyLong(), anyLong(), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("fail!"));
        String json = "{\"resourceId\":2,\"studentId\":3,\"progress\":100,\"position\":200}";
        mockMvc.perform(post("/api/resources/progress")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("更新学习进度失败：")));
    }

    // getProgress success/not found/fail
    @Test
    void getProgress_success() throws Exception {
        LearningProgress lp = new LearningProgress();
        Mockito.when(resourceService.getProgress(1L, 2L)).thenReturn(lp);
        mockMvc.perform(get("/api/resources/progress/1/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("获取学习进度成功"));
    }
    @Test
    void getProgress_notFound() throws Exception {
        Mockito.when(resourceService.getProgress(1L, 2L)).thenReturn(null);
        mockMvc.perform(get("/api/resources/progress/1/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("未找到学习进度记录"));
    }
    @Test
    void getProgress_fail() throws Exception {
        Mockito.when(resourceService.getProgress(1L, 2L)).thenThrow(new RuntimeException("fail!"));
        mockMvc.perform(get("/api/resources/progress/1/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("获取学习进度失败：")));
    }

    // getResourceUrl success/not found/fail/businessException
    @Test
    void getResourceUrl_success() throws Exception {
        TeachingResource r = new TeachingResource();
        r.setId(9L);
        Mockito.when(resourceService.getResource(9L)).thenReturn(r);
        Mockito.when(resourceService.getSignedResourceUrl(9L)).thenReturn("http://url");

        mockMvc.perform(get("/api/resources/url/9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("获取资源访问链接成功（有效期1小时）"))
                .andExpect(jsonPath("$.data").value("http://url"));
    }
    @Test
    void getResourceUrl_notFound() throws Exception {
        Mockito.when(resourceService.getResource(9L)).thenReturn(null);
        mockMvc.perform(get("/api/resources/url/9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("资源不存在"));
    }
    @Test
    void getResourceUrl_nullUrl() throws Exception {
        TeachingResource r = new TeachingResource();
        r.setId(9L);
        Mockito.when(resourceService.getResource(9L)).thenReturn(r);
        Mockito.when(resourceService.getSignedResourceUrl(9L)).thenReturn(null);

        mockMvc.perform(get("/api/resources/url/9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("获取资源访问链接失败"));
    }
    @Test
    void getResourceUrl_businessException() throws Exception {
        TeachingResource r = new TeachingResource();
        r.setId(9L);
        Mockito.when(resourceService.getResource(9L)).thenReturn(r);
        Mockito.when(resourceService.getSignedResourceUrl(9L)).thenThrow(new BusinessException("业务异常"));
        mockMvc.perform(get("/api/resources/url/9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("业务异常"));
    }
    @Test
    void getResourceUrl_fail() throws Exception {
        TeachingResource r = new TeachingResource();
        r.setId(9L);
        Mockito.when(resourceService.getResource(9L)).thenReturn(r);
        Mockito.when(resourceService.getSignedResourceUrl(9L)).thenThrow(new RuntimeException("fail!"));
        mockMvc.perform(get("/api/resources/url/9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("获取资源访问链接失败：")));
    }

    // getCourseResourcesWithProgress success/fail
    @Test
    void getCourseResourcesWithProgress_success() throws Exception {
        List<ResourceProgressDTO> list = Arrays.asList(new ResourceProgressDTO(), new ResourceProgressDTO());
        Mockito.when(resourceService.getCourseResourcesWithProgress(1L, 2L, 3L)).thenReturn(list);

        String json = "{\"studentId\":2,\"chapterId\":3}";
        mockMvc.perform(post("/api/resources/chapter/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("获取课程资源及进度信息成功"))
                .andExpect(jsonPath("$.data[0]").exists());
    }
    @Test
    void getCourseResourcesWithProgress_fail() throws Exception {
        Mockito.when(resourceService.getCourseResourcesWithProgress(1L, 2L, 3L)).thenThrow(new RuntimeException("fail!"));
        String json = "{\"studentId\":2,\"chapterId\":3}";
        mockMvc.perform(post("/api/resources/chapter/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("获取课程资源及进度信息失败")));
    }

    // updateResourceDuration success/fail
    @Test
    void updateResourceDuration_success() throws Exception {
        TeachingResource r = new TeachingResource();
        r.setId(6L);
        Mockito.when(resourceService.updateResourceDuration(6L, 120)).thenReturn(r);

        String json = "{\"duration\":120}";
        mockMvc.perform(put("/api/resources/6/duration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("更新视频时长成功"))
                .andExpect(jsonPath("$.data.id").value(6));
    }
    @Test
    void updateResourceDuration_noDuration() throws Exception {
        String json = "{}";
        mockMvc.perform(put("/api/resources/6/duration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("视频时长不能为空"));
    }
    @Test
    void updateResourceDuration_fail() throws Exception {
        Mockito.when(resourceService.updateResourceDuration(6L, 120)).thenThrow(new RuntimeException("fail!"));
        String json = "{\"duration\":120}";
        mockMvc.perform(put("/api/resources/6/duration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("更新视频时长失败：")));
    }
}