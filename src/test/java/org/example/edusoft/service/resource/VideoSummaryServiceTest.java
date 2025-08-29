package org.example.edusoft.service.resource;

import org.example.edusoft.dto.resource.VideoSummaryDetailDTO;
import org.example.edusoft.entity.resource.VideoSummary;
import org.example.edusoft.mapper.resource.VideoSummaryMapper;
import org.example.edusoft.mapper.resource.VideoSummaryStageMapper;
import org.example.edusoft.mapper.resource.VideoSummaryKeypointMapper;
import org.example.edusoft.mapper.resource.VideoSummaryTimestampMapper;
import org.example.edusoft.service.resource.impl.VideoSummaryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoSummaryServiceTest {

    @Mock
    private VideoSummaryMapper videoSummaryMapper;
    @Mock
    private VideoSummaryStageMapper stageMapper;
    @Mock
    private VideoSummaryKeypointMapper keypointMapper;
    @Mock
    private VideoSummaryTimestampMapper timestampMapper;

    @Spy
    @InjectMocks
    private VideoSummaryServiceImpl videoSummaryService;

    @Test
    @DisplayName("根据资源ID获取视频摘要 - 摘要存在")
    void testGetSummaryByResourceId_found() {
        VideoSummary summary = new VideoSummary();
        summary.setId(1L);
        summary.setResourceId(100L);
        when(videoSummaryMapper.selectByResourceId(100L)).thenReturn(summary);
        when(stageMapper.selectBySummaryId(1L)).thenReturn(Collections.emptyList());
        when(keypointMapper.selectBySummaryId(1L)).thenReturn(Collections.emptyList());
        when(timestampMapper.selectBySummaryId(1L)).thenReturn(Collections.emptyList());

        VideoSummaryDetailDTO result = videoSummaryService.getSummaryByResourceId(100L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("根据资源ID获取视频摘要 - 摘要不存在")
    void testGetSummaryByResourceId_notFound() {
        when(videoSummaryMapper.selectByResourceId(200L)).thenReturn(null);

        VideoSummaryDetailDTO result = videoSummaryService.getSummaryByResourceId(200L);

        assertNull(result);
    }

    @Test
    @DisplayName("根据摘要ID获取视频摘要 - 摘要存在")
    void testGetSummaryById_found() {
        VideoSummary summary = new VideoSummary();
        summary.setId(1L);
        when(videoSummaryMapper.selectById(1L)).thenReturn(summary);
        when(stageMapper.selectBySummaryId(1L)).thenReturn(Collections.emptyList());
        when(keypointMapper.selectBySummaryId(1L)).thenReturn(Collections.emptyList());
        when(timestampMapper.selectBySummaryId(1L)).thenReturn(Collections.emptyList());

        VideoSummaryDetailDTO result = videoSummaryService.getSummaryById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("根据摘要ID获取视频摘要 - 摘要不存在")
    void testGetSummaryById_notFound() {
        when(videoSummaryMapper.selectById(2L)).thenReturn(null);

        VideoSummaryDetailDTO result = videoSummaryService.getSummaryById(2L);

        assertNull(result);
    }

    @Test
    @DisplayName("删除视频摘要 - 删除成功")
    void testDeleteSummary_success() {
        when(videoSummaryMapper.delete(1L)).thenReturn(1);

        boolean result = videoSummaryService.deleteSummary(1L);

        assertTrue(result);
        verify(stageMapper).deleteBySummaryId(1L);
        verify(keypointMapper).deleteBySummaryId(1L);
        verify(timestampMapper).deleteBySummaryId(1L);
    }

    @Test
    @DisplayName("删除视频摘要 - 删除失败")
    void testDeleteSummary_fail() {
        when(videoSummaryMapper.delete(2L)).thenReturn(0);

        boolean result = videoSummaryService.deleteSummary(2L);

        assertFalse(result);
        verify(stageMapper).deleteBySummaryId(2L);
        verify(keypointMapper).deleteBySummaryId(2L);
        verify(timestampMapper).deleteBySummaryId(2L);
    }

    @Test
    @DisplayName("根据资源ID删除视频摘要 - 摘要存在")
    void testDeleteSummaryByResourceId_found() {
        VideoSummary summary = new VideoSummary();
        summary.setId(1L);
        when(videoSummaryMapper.selectByResourceId(100L)).thenReturn(summary);
        when(videoSummaryMapper.delete(1L)).thenReturn(1);

        boolean result = videoSummaryService.deleteSummaryByResourceId(100L);

        assertTrue(result);
        verify(stageMapper).deleteBySummaryId(1L);
        verify(keypointMapper).deleteBySummaryId(1L);
        verify(timestampMapper).deleteBySummaryId(1L);
    }

    @Test
    @DisplayName("根据资源ID删除视频摘要 - 摘要不存在")
    void testDeleteSummaryByResourceId_notFound() {
        when(videoSummaryMapper.selectByResourceId(200L)).thenReturn(null);

        boolean result = videoSummaryService.deleteSummaryByResourceId(200L);

        assertTrue(result);
    }

    @Test
    @DisplayName("重新生成视频摘要 - 成功")
    void testRegenerateSummary_success() {
        VideoSummary summary = new VideoSummary();
        summary.setId(1L);
        when(videoSummaryMapper.selectByResourceId(100L)).thenReturn(summary);
        when(videoSummaryMapper.delete(1L)).thenReturn(1);

        VideoSummaryDetailDTO newSummary = new VideoSummaryDetailDTO();
        newSummary.setId(2L);
        doReturn(newSummary).when(videoSummaryService).generateSummaryForResource(100L);

        VideoSummaryDetailDTO result = videoSummaryService.regenerateSummary(100L);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        verify(stageMapper).deleteBySummaryId(1L);
        verify(keypointMapper).deleteBySummaryId(1L);
        verify(timestampMapper).deleteBySummaryId(1L);
    }

    @Test
    @DisplayName("重新生成视频摘要 - 失败")
    void testRegenerateSummary_fail() {
        when(videoSummaryMapper.selectByResourceId(200L)).thenReturn(null);

        doThrow(new RuntimeException("生成失败")).when(videoSummaryService).generateSummaryForResource(200L);

        assertThrows(RuntimeException.class, () -> videoSummaryService.regenerateSummary(200L));
    }
}
