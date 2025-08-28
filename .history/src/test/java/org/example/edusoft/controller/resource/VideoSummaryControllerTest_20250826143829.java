package org.example.edusoft.controller.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.edusoft.dto.resource.VideoSummaryDetailDTO;
import org.example.edusoft.service.resource.VideoSummaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class VideoSummaryControllerTest {

    @Mock
    private VideoSummaryService videoSummaryService;

    @InjectMocks
    private VideoSummaryController videoSummaryController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(videoSummaryController).build();
        objectMapper = new ObjectMapper();
    }

    // ===================== generateSummary 测试 =====================

    @Test
    void generateSummary_Success() throws Exception {
        // 准备测试数据
        Long resourceId = 1L;
        VideoSummaryDetailDTO mockSummary = createMockVideoSummaryDetailDTO(1L, "Java基础教学视频摘要");

        when(videoSummaryService.generateSummaryForResource(resourceId)).thenReturn(mockSummary);

        // 执行测试
        mockMvc.perform(post("/api/video-summary/generate/{resourceId}", resourceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.summary").value("Java基础教学视频摘要"));

        verify(videoSummaryService, times(1)).generateSummaryForResource(resourceId);
    }

    @Test
    void generateSummary_ServiceThrowsException() throws Exception {
        // 准备测试数据
        Long resourceId = 999L;

        when(videoSummaryService.generateSummaryForResource(resourceId))
                .thenThrow(new RuntimeException("资源不存在"));

        // 执行测试
        mockMvc.perform(post("/api/video-summary/generate/{resourceId}", resourceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("生成视频摘要失败: 资源不存在"));

        verify(videoSummaryService, times(1)).generateSummaryForResource(resourceId);
    }

    // ===================== getSummaryByResourceId 测试 =====================

    @Test
    void getSummaryByResourceId_Success() throws Exception {
        // 准备测试数据
        Long resourceId = 1L;
        VideoSummaryDetailDTO mockSummary = createMockVideoSummaryDetailDTO(1L, "Spring框架教学视频摘要");

        when(videoSummaryService.getSummaryByResourceId(resourceId)).thenReturn(mockSummary);

        // 执行测试
        mockMvc.perform(get("/api/video-summary/resource/{resourceId}", resourceId))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.summary").value("Spring框架教学视频摘要"));

        verify(videoSummaryService, times(1)).getSummaryByResourceId(resourceId);
    }

    @Test
    void getSummaryByResourceId_NotFound() throws Exception {
        // 准备测试数据
        Long resourceId = 999L;

        when(videoSummaryService.getSummaryByResourceId(resourceId)).thenReturn(null);

        // 执行测试
        mockMvc.perform(get("/api/video-summary/resource/{resourceId}", resourceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpected(jsonPath("$.message").value("视频摘要不存在"));

        verify(videoSummaryService, times(1)).getSummaryByResourceId(resourceId);
    }

    @Test
    void getSummaryByResourceId_ServiceThrowsException() throws Exception {
        // 准备测试数据
        Long resourceId = 1L;

        when(videoSummaryService.getSummaryByResourceId(resourceId))
                .thenThrow(new RuntimeException("数据库连接失败"));

        // 执行测试
        mockMvc.perform(get("/api/video-summary/resource/{resourceId}", resourceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("获取视频摘要失败: 数据库连接失败"));

        verify(videoSummaryService, times(1)).getSummaryByResourceId(resourceId);
    }

    // ===================== getSummaryById 测试 =====================

    @Test
    void getSummaryById_Success() throws Exception {
        // 准备测试数据
        Long summaryId = 2L;
        VideoSummaryDetailDTO mockSummary = createMockVideoSummaryDetailDTO(summaryId, "MySQL数据库教学视频摘要");

        when(videoSummaryService.getSummaryById(summaryId)).thenReturn(mockSummary);

        // 执行测试
        mockMvc.perform(get("/api/video-summary/{summaryId}", summaryId))
                .andExpect(status().isOk())
                .andExpected(jsonPath("$.code").value(200))
                .andExpected(jsonPath("$.data.id").value(2))
                .andExpected(jsonPath("$.data.summary").value("MySQL数据库教学视频摘要"));

        verify(videoSummaryService, times(1)).getSummaryById(summaryId);
    }

    @Test
    void getSummaryById_NotFound() throws Exception {
        // 准备测试数据
        Long summaryId = 999L;

        when(videoSummaryService.getSummaryById(summaryId)).thenReturn(null);

        // 执行测试
        mockMvc.perform(get("/api/video-summary/{summaryId}", summaryId))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("视频摘要不存在"));

        verify(videoSummaryService, times(1)).getSummaryById(summaryId);
    }

    @Test
    void getSummaryById_ServiceThrowsException() throws Exception {
        // 准备测试数据
        Long summaryId = 1L;

        when(videoSummaryService.getSummaryById(summaryId))
                .thenThrow(new RuntimeException("权限不足"));

        // 执行测试
        mockMvc.perform(get("/api/video-summary/{summaryId}", summaryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("获取视频摘要失败: 权限不足"));

        verify(videoSummaryService, times(1)).getSummaryById(summaryId);
    }

    // ===================== regenerateSummary 测试 =====================

    @Test
    void regenerateSummary_Success() throws Exception {
        // 准备测试数据
        Long resourceId = 1L;
        VideoSummaryDetailDTO mockSummary = createMockVideoSummaryDetailDTO(1L, "重新生成的Java基础教学视频摘要");

        when(videoSummaryService.regenerateSummary(resourceId)).thenReturn(mockSummary);

        // 执行测试
        mockMvc.perform(post("/api/video-summary/regenerate/{resourceId}", resourceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpected(jsonPath("$.data.summary").value("重新生成的Java基础教学视频摘要"));

        verify(videoSummaryService, times(1)).regenerateSummary(resourceId);
    }

    @Test
    void regenerateSummary_ServiceThrowsException() throws Exception {
        // 准备测试数据
        Long resourceId = 1L;

        when(videoSummaryService.regenerateSummary(resourceId))
                .thenThrow(new RuntimeException("AI服务不可用"));

        // 执行测试
        mockMvc.perform(post("/api/video-summary/regenerate/{resourceId}", resourceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpected(jsonPath("$.message").value("重新生成视频摘要失败: AI服务不可用"));

        verify(videoSummaryService, times(1)).regenerateSummary(resourceId);
    }

    // ===================== deleteSummary 测试 =====================

    @Test
    void deleteSummary_Success() throws Exception {
        // 准备测试数据
        Long summaryId = 1L;

        when(videoSummaryService.deleteSummary(summaryId)).thenReturn(true);

        // 执行测试
        mockMvc.perform(delete("/api/video-summary/{summaryId}", summaryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpected(jsonPath("$.data").value("删除成功"));

        verify(videoSummaryService, times(1)).deleteSummary(summaryId);
    }

    @Test
    void deleteSummary_Failed() throws Exception {
        // 准备测试数据
        Long summaryId = 999L;

        when(videoSummaryService.deleteSummary(summaryId)).thenReturn(false);

        // 执行测试
        mockMvc.perform(delete("/api/video-summary/{summaryId}", summaryId))
                .andExpect(status().isOk())
                .andExpected(jsonPath("$.code").value(500))
                .andExpected(jsonPath("$.message").value("删除失败"));

        verify(videoSummaryService, times(1)).deleteSummary(summaryId);
    }

    @Test
    void deleteSummary_ServiceThrowsException() throws Exception {
        // 准备测试数据
        Long summaryId = 1L;

        when(videoSummaryService.deleteSummary(summaryId))
                .thenThrow(new RuntimeException("数据库约束违反"));

        // 执行测试
        mockMvc.perform(delete("/api/video-summary/{summaryId}", summaryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpected(jsonPath("$.message").value("删除视频摘要失败: 数据库约束违反"));

        verify(videoSummaryService, times(1)).deleteSummary(summaryId);
    }

    // ===================== deleteSummaryByResourceId 测试 =====================

    @Test
    void deleteSummaryByResourceId_Success() throws Exception {
        // 准备测试数据
        Long resourceId = 1L;

        when(videoSummaryService.deleteSummaryByResourceId(resourceId)).thenReturn(true);

        // 执行测试
        mockMvc.perform(delete("/api/video-summary/resource/{resourceId}", resourceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpected(jsonPath("$.data").value("删除成功"));

        verify(videoSummaryService, times(1)).deleteSummaryByResourceId(resourceId);
    }

    @Test
    void deleteSummaryByResourceId_Failed() throws Exception {
        // 准备测试数据
        Long resourceId = 999L;

        when(videoSummaryService.deleteSummaryByResourceId(resourceId)).thenReturn(false);

        // 执行测试
        mockMvc.perform(delete("/api/video-summary/resource/{resourceId}", resourceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpected(jsonPath("$.message").value("删除失败"));

        verify(videoSummaryService, times(1)).deleteSummaryByResourceId(resourceId);
    }

    @Test
    void deleteSummaryByResourceId_ServiceThrowsException() throws Exception {
        // 准备测试数据
        Long resourceId = 1L;

        when(videoSummaryService.deleteSummaryByResourceId(resourceId))
                .thenThrow(new RuntimeException("无权限删除"));

        // 执行测试
        mockMvc.perform(delete("/api/video-summary/resource/{resourceId}", resourceId))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.code").value(500))
                .andExpected(jsonPath("$.message").value("删除视频摘要失败: 无权限删除"));

        verify(videoSummaryService, times(1)).deleteSummaryByResourceId(resourceId);
    }

    // ===================== 辅助方法 =====================

    private VideoSummaryDetailDTO createMockVideoSummaryDetailDTO(Long id, String summary) {
        VideoSummaryDetailDTO dto = new VideoSummaryDetailDTO();
        dto.setId(id);
        dto.setSummary(summary);
        dto.setResourceId(1L);
        dto.setKeyPoints(Arrays.asList("关键点1", "关键点2", "关键点3"));
        dto.setDuration(1800); // 30分钟
        dto.setCreateTime(new Date());
        return dto;
    }
}