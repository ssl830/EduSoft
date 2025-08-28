package org.example.edusoft.service.discussion;

import org.example.edusoft.entity.discussion.Discussion;
import org.example.edusoft.entity.discussion.DiscussionLike;
import org.example.edusoft.mapper.discussion.DiscussionLikeMapper;
import org.example.edusoft.service.discussion.impl.DiscussionLikeServiceImpl;
import org.example.edusoft.service.discussion.DiscussionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscussionLikeServiceTest {

    @Mock
    private DiscussionLikeMapper discussionLikeMapper;

    @Mock
    private DiscussionService discussionService;

    @InjectMocks
    private DiscussionLikeServiceImpl discussionLikeService;

    @Test
    @DisplayName("点赞讨论 - 成功")
    void testLikeDiscussion_success() {
        Long discussionId = 1L;
        Long userId = 2L;
        when(discussionService.getDiscussion(discussionId)).thenReturn(new Discussion());
        when(discussionLikeMapper.findByDiscussionAndUser(discussionId, userId)).thenReturn(null);

        DiscussionLike like = discussionLikeService.likeDiscussion(discussionId, userId);

        assertEquals(discussionId, like.getDiscussionId());
        assertEquals(userId, like.getUserId());
        assertNotNull(like.getCreatedAt());
        verify(discussionLikeMapper, times(1)).insert(any(DiscussionLike.class));
    }

    @Test
    @DisplayName("点赞讨论 - 讨论不存在")
    void testLikeDiscussion_discussionNotExist() {
        Long discussionId = 1L;
        Long userId = 2L;
        when(discussionService.getDiscussion(discussionId)).thenReturn(null);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> discussionLikeService.likeDiscussion(discussionId, userId));
        assertEquals("讨论不存在", ex.getMessage());
    }

    @Test
    @DisplayName("点赞讨论 - 已点赞返回原记录")
    void testLikeDiscussion_alreadyLiked() {
        Long discussionId = 1L;
        Long userId = 2L;
        DiscussionLike like = new DiscussionLike();
        like.setDiscussionId(discussionId);
        like.setUserId(userId);
        like.setCreatedAt(LocalDateTime.now());
        when(discussionService.getDiscussion(discussionId)).thenReturn(new Discussion());
        when(discussionLikeMapper.findByDiscussionAndUser(discussionId, userId)).thenReturn(like);

        DiscussionLike result = discussionLikeService.likeDiscussion(discussionId, userId);

        assertEquals(like, result);
        verify(discussionLikeMapper, never()).insert(any());
    }

    @Test
    @DisplayName("取消点赞 - 正常")
    void testUnlikeDiscussion_success() {
        Long discussionId = 1L;
        Long userId = 2L;
        // 修正：无需 doNothing，直接调用即可
        discussionLikeService.unlikeDiscussion(discussionId, userId);

        verify(discussionLikeMapper, times(1)).deleteByDiscussionAndUser(discussionId, userId);
    }

    @Test
    @DisplayName("获取讨论的点赞列表 - 正常")
    void testGetLikesByDiscussion_success() {
        Long discussionId = 1L;
        List<DiscussionLike> likes = Arrays.asList(new DiscussionLike(), new DiscussionLike());
        when(discussionLikeMapper.findByDiscussionId(discussionId)).thenReturn(likes);

        List<DiscussionLike> result = discussionLikeService.getLikesByDiscussion(discussionId);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("获取讨论的点赞列表 - 空列表")
    void testGetLikesByDiscussion_empty() {
        Long discussionId = 1L;
        when(discussionLikeMapper.findByDiscussionId(discussionId)).thenReturn(Collections.emptyList());

        List<DiscussionLike> result = discussionLikeService.getLikesByDiscussion(discussionId);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("获取用户的点赞列表 - 正常")
    void testGetLikesByUser_success() {
        Long userId = 2L;
        List<DiscussionLike> likes = Arrays.asList(new DiscussionLike());
        when(discussionLikeMapper.findByUserId(userId)).thenReturn(likes);

        List<DiscussionLike> result = discussionLikeService.getLikesByUser(userId);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("获取用户的点赞列表 - 空列表")
    void testGetLikesByUser_empty() {
        Long userId = 2L;
        when(discussionLikeMapper.findByUserId(userId)).thenReturn(Collections.emptyList());

        List<DiscussionLike> result = discussionLikeService.getLikesByUser(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("获取讨论的点赞数 - 正常")
    void testCountLikesByDiscussion_success() {
        Long discussionId = 1L;
        when(discussionLikeMapper.countByDiscussionId(discussionId)).thenReturn(5);

        int count = discussionLikeService.countLikesByDiscussion(discussionId);

        assertEquals(5, count);
    }

    @Test
    @DisplayName("获取讨论的点赞数 - 为0")
    void testCountLikesByDiscussion_zero() {
        Long discussionId = 1L;
        when(discussionLikeMapper.countByDiscussionId(discussionId)).thenReturn(0);

        int count = discussionLikeService.countLikesByDiscussion(discussionId);

        assertEquals(0, count);
    }

    @Test
    @DisplayName("获取用户的点赞数 - 正常")
    void testCountLikesByUser_success() {
        Long userId = 2L;
        when(discussionLikeMapper.countByUserId(userId)).thenReturn(3);

        int count = discussionLikeService.countLikesByUser(userId);

        assertEquals(3, count);
    }

    @Test
    @DisplayName("获取用户的点赞数 - 为0")
    void testCountLikesByUser_zero() {
        Long userId = 2L;
        when(discussionLikeMapper.countByUserId(userId)).thenReturn(0);

        int count = discussionLikeService.countLikesByUser(userId);

        assertEquals(0, count);
    }

    @Test
    @DisplayName("检查用户是否已点赞 - 已点赞")
    void testHasLiked_true() {
        Long discussionId = 1L;
        Long userId = 2L;
        when(discussionLikeMapper.findByDiscussionAndUser(discussionId, userId)).thenReturn(new DiscussionLike());

        boolean result = discussionLikeService.hasLiked(discussionId, userId);

        assertTrue(result);
    }

    @Test
    @DisplayName("检查用户是否已点赞 - 未点赞")
    void testHasLiked_false() {
        Long discussionId = 1L;
        Long userId = 2L;
        when(discussionLikeMapper.findByDiscussionAndUser(discussionId, userId)).thenReturn(null);

        boolean result = discussionLikeService.hasLiked(discussionId, userId);

        assertFalse(result);
    }
}
