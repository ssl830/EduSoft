package org.example.edusoft.service.discussion;

import org.example.edusoft.entity.discussion.Discussion;
import org.example.edusoft.mapper.discussion.DiscussionMapper;
import org.example.edusoft.service.discussion.impl.DiscussionServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscussionServiceTest {

    @Mock
    private DiscussionMapper discussionMapper;

    @InjectMocks
    private DiscussionServiceImpl discussionService;

    @Test
    @DisplayName("创建讨论 - 正常")
    void testCreateDiscussion_success() {
        Discussion discussion = new Discussion();
        discussion.setTitle("test");
        when(discussionMapper.insert(any())).thenReturn(1);

        Discussion result = discussionService.createDiscussion(discussion);

        assertEquals("test", result.getTitle());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        verify(discussionMapper, times(1)).insert(any());
    }

    @Test
    @DisplayName("创建讨论 - discussion为null")
    void testCreateDiscussion_null() {
        Exception ex = assertThrows(NullPointerException.class, () -> discussionService.createDiscussion(null));
        assertNotNull(ex);
    }

    @Test
    @DisplayName("更新讨论 - 正常")
    void testUpdateDiscussion_success() {
        Discussion discussion = new Discussion();
        discussion.setId(1L);
        discussion.setTitle("new title");

        Discussion existing = new Discussion();
        existing.setId(1L);
        existing.setTitle("old title");
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));

        when(discussionMapper.selectById(1L)).thenReturn(existing);
        when(discussionMapper.updateById(any())).thenReturn(1);

        Discussion result = discussionService.updateDiscussion(discussion);

        assertEquals(existing.getCreatedAt(), result.getCreatedAt());
        assertEquals("new title", result.getTitle());
        verify(discussionMapper, times(1)).updateById(any());
    }

    @Test
    @DisplayName("更新讨论 - 不存在")
    void testUpdateDiscussion_notExist() {
        Discussion discussion = new Discussion();
        discussion.setId(2L);
        when(discussionMapper.selectById(2L)).thenReturn(null);
        when(discussionMapper.updateById(any())).thenReturn(1);

        Discussion result = discussionService.updateDiscussion(discussion);

        assertNull(result.getCreatedAt());
        verify(discussionMapper, times(1)).updateById(any());
    }

    @Test
    @DisplayName("删除讨论 - 正常")
    void testDeleteDiscussion_success() {
        discussionService.deleteDiscussion(1L);

        verify(discussionMapper, times(1)).deleteById(eq(1L));
    }

    @Test
    @DisplayName("删除讨论 - id为null")
    void testDeleteDiscussion_idNull() {
        discussionService.deleteDiscussion(null);
        verify(discussionMapper, never()).deleteById(any(Serializable.class));
    }

    @Test
    @DisplayName("获取讨论详情 - 正常")
    void testGetDiscussion_success() {
        Discussion discussion = new Discussion();
        discussion.setId(1L);
        when(discussionMapper.selectById(1L)).thenReturn(discussion);

        Discussion result = discussionService.getDiscussion(1L);

        assertEquals(discussion, result);
    }

    @Test
    @DisplayName("获取讨论详情 - 不存在")
    void testGetDiscussion_notExist() {
        when(discussionMapper.selectById(2L)).thenReturn(null);

        Discussion result = discussionService.getDiscussion(2L);

        assertNull(result);
    }

    @Test
    @DisplayName("获取课程下的讨论列表 - 正常")
    void testGetDiscussionsByCourse_success() {
        List<Discussion> discussions = Arrays.asList(new Discussion(), new Discussion());
        when(discussionMapper.findByCourseId(1L)).thenReturn(discussions);

        List<Discussion> result = discussionService.getDiscussionsByCourse(1L);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("获取课程下的讨论列表 - 空列表")
    void testGetDiscussionsByCourse_empty() {
        when(discussionMapper.findByCourseId(1L)).thenReturn(Collections.emptyList());

        List<Discussion> result = discussionService.getDiscussionsByCourse(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("获取班级下的讨论��表 - 正常")
    void testGetDiscussionsByClass_success() {
        List<Discussion> discussions = Arrays.asList(new Discussion());
        when(discussionMapper.findByClassId(2L)).thenReturn(discussions);

        List<Discussion> result = discussionService.getDiscussionsByClass(2L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("获取班级下的讨论列表 - 空列表")
    void testGetDiscussionsByClass_empty() {
        when(discussionMapper.findByClassId(2L)).thenReturn(Collections.emptyList());

        List<Discussion> result = discussionService.getDiscussionsByClass(2L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("获取用户创建的讨论列表 - 正常")
    void testGetDiscussionsByCreator_success() {
        List<Discussion> discussions = Arrays.asList(new Discussion());
        when(discussionMapper.findByCreatorId(3L)).thenReturn(discussions);

        List<Discussion> result = discussionService.getDiscussionsByCreator(3L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("获取用户创建的讨论列表 - 空列表")
    void testGetDiscussionsByCreator_empty() {
        when(discussionMapper.findByCreatorId(3L)).thenReturn(Collections.emptyList());

        List<Discussion> result = discussionService.getDiscussionsByCreator(3L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("获取特定课程和班级的讨论列表 - 正常")
    void testGetDiscussionsByCourseAndClass_success() {
        List<Discussion> discussions = Arrays.asList(new Discussion());
        when(discussionMapper.findByCourseAndClass(1L, 2L)).thenReturn(discussions);

        List<Discussion> result = discussionService.getDiscussionsByCourseAndClass(1L, 2L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("获取特定课程和班级的讨论列表 - 空列表")
    void testGetDiscussionsByCourseAndClass_empty() {
        when(discussionMapper.findByCourseAndClass(1L, 2L)).thenReturn(Collections.emptyList());

        List<Discussion> result = discussionService.getDiscussionsByCourseAndClass(1L, 2L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("增加浏览次数 - 正常")
    void testIncrementViewCount_success() {
        // 无需 doNothing
        discussionService.incrementViewCount(1L);

        verify(discussionMapper, times(1)).incrementViewCount(1L);
    }

    @Test
    @DisplayName("增加浏览次数 - id为null")
    void testIncrementViewCount_idNull() {
        // 不断言异常，直接调用并验证未调用
        discussionService.incrementViewCount(null);
        verify(discussionMapper, never()).incrementViewCount(any());
    }

    @Test
    @DisplayName("更新置顶状态 - 正常")
    void testUpdatePinnedStatus_success() {
        // 无需 doNothing
        discussionService.updatePinnedStatus(1L, true);

        verify(discussionMapper, times(1)).updatePinnedStatus(1L, true);
    }

    @Test
    @DisplayName("更新置顶状态 - id为null")
    void testUpdatePinnedStatus_idNull() {
        // 不断言异常，直接调用并验证未调用
        discussionService.updatePinnedStatus(null, true);
        verify(discussionMapper, never()).updatePinnedStatus(any(), any());
    }

    @Test
    @DisplayName("更新关闭状态 - 正常")
    void testUpdateClosedStatus_success() {
        // 无需 doNothing
        discussionService.updateClosedStatus(1L, true);

        verify(discussionMapper, times(1)).updateClosedStatus(1L, true);
    }

    @Test
    @DisplayName("更新关闭状态 - id为null")
    void testUpdateClosedStatus_idNull() {
        // 不断言异常，直接调用并验证未调用
        discussionService.updateClosedStatus(null, true);
        verify(discussionMapper, never()).updateClosedStatus(any(), any());
    }

    @Test
    @DisplayName("统计课程讨论数 - 正常")
    void testCountDiscussionsByCourse_success() {
        when(discussionMapper.countByCourseId(1L)).thenReturn(5);

        int count = discussionService.countDiscussionsByCourse(1L);

        assertEquals(5, count);
    }

    @Test
    @DisplayName("统计课程讨论数 - 为0")
    void testCountDiscussionsByCourse_zero() {
        when(discussionMapper.countByCourseId(1L)).thenReturn(0);

        int count = discussionService.countDiscussionsByCourse(1L);

        assertEquals(0, count);
    }

    @Test
    @DisplayName("统计班级讨论数 - 正常")
    void testCountDiscussionsByClass_success() {
        when(discussionMapper.countByClassId(2L)).thenReturn(3);

        int count = discussionService.countDiscussionsByClass(2L);

        assertEquals(3, count);
    }

    @Test
    @DisplayName("统计班级讨论数 - 为0")
    void testCountDiscussionsByClass_zero() {
        when(discussionMapper.countByClassId(2L)).thenReturn(0);

        int count = discussionService.countDiscussionsByClass(2L);

        assertEquals(0, count);
    }
}
