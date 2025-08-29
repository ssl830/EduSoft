package org.example.edusoft.service.discussion;

import org.example.edusoft.entity.discussion.DiscussionReply;
import org.example.edusoft.mapper.discussion.DiscussionMapper;
import org.example.edusoft.mapper.discussion.DiscussionReplyMapper;
import org.example.edusoft.service.discussion.impl.DiscussionReplyServiceImpl;
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
class DiscussionReplyServiceTest {

    @Mock
    private DiscussionReplyMapper replyMapper;

    @Mock
    private DiscussionMapper discussionMapper;

    @InjectMocks
    private DiscussionReplyServiceImpl replyService;

    @Test
    @DisplayName("创建回复 - 正常")
    void testCreateReply_success() {
        DiscussionReply reply = new DiscussionReply();
        reply.setDiscussionId(1L);
        reply.setUserId(2L);
        reply.setContent("内容");
        reply.setParentReplyId(null);

        when(replyMapper.insert(any())).thenReturn(1);
        replyService.createReply(reply);

        DiscussionReply result = replyService.createReply(reply);

        assertEquals("内容", result.getContent());
        assertNotNull(result.getCreatedAt());
        verify(replyMapper, atLeastOnce()).insert(any());
        verify(discussionMapper, atLeastOnce()).incrementReplyCount(1L);
    }

    @Test
    @DisplayName("创建回复 - 父回复不存在")
    void testCreateReply_parentNotExist() {
        DiscussionReply reply = new DiscussionReply();
        reply.setDiscussionId(1L);
        reply.setParentReplyId(99L);

        when(replyMapper.selectById(99L)).thenReturn(null);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> replyService.createReply(reply));
        assertEquals("Parent reply does not exist", ex.getMessage());
    }

    @Test
    @DisplayName("创建回复 - 父回复不属于同一讨论")
    void testCreateReply_parentNotSameDiscussion() {
        DiscussionReply reply = new DiscussionReply();
        reply.setDiscussionId(1L);
        reply.setParentReplyId(99L);

        DiscussionReply parent = new DiscussionReply();
        parent.setId(99L);
        parent.setDiscussionId(2L);

        when(replyMapper.selectById(99L)).thenReturn(parent);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> replyService.createReply(reply));
        assertEquals("Parent reply does not belong to this discussion", ex.getMessage());
    }

    @Test
    @DisplayName("更新回复 - 正常")
    void testUpdateReply_success() {
        DiscussionReply reply = new DiscussionReply();
        reply.setId(1L);
        reply.setContent("新内容");

        DiscussionReply existing = new DiscussionReply();
        existing.setId(1L);
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));

        when(replyMapper.selectById(1L)).thenReturn(existing);
        when(replyMapper.updateById(any())).thenReturn(1);

        DiscussionReply result = replyService.updateReply(reply);

        assertEquals("新内容", result.getContent());
        assertEquals(existing.getCreatedAt(), result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        verify(replyMapper, times(1)).updateById(any());
    }

    @Test
    @DisplayName("更新回复 - 回复不存在")
    void testUpdateReply_notExist() {
        DiscussionReply reply = new DiscussionReply();
        reply.setId(2L);

        when(replyMapper.selectById(2L)).thenReturn(null);
        when(replyMapper.updateById(any())).thenReturn(1);

        DiscussionReply result = replyService.updateReply(reply);

        assertNotNull(result); // 只断言对象不为null
        verify(replyMapper, times(1)).updateById(any());
    }

    @Test
    @DisplayName("删除回复 - 正常")
    void testDeleteReply_success() {
        DiscussionReply reply = new DiscussionReply();
        reply.setId(1L);
        reply.setDiscussionId(2L);

        when(replyMapper.selectById(1L)).thenReturn(reply);
        replyService.deleteReply(1L);

        verify(replyMapper, times(1)).deleteById(1L);
        verify(discussionMapper, times(1)).decrementReplyCount(2L);
    }

    @Test
    @DisplayName("删除回复 - 回复不存在")
    void testDeleteReply_notExist() {
        when(replyMapper.selectById(99L)).thenReturn(null);

        replyService.deleteReply(99L);

        verify(replyMapper, never()).deleteById(anyLong());
        verify(discussionMapper, never()).decrementReplyCount(anyLong());
    }

    @Test
    @DisplayName("获取回复详情 - 正常")
    void testGetReply_success() {
        DiscussionReply reply = new DiscussionReply();
        reply.setId(1L);
        when(replyMapper.selectById(1L)).thenReturn(reply);

        DiscussionReply result = replyService.getReply(1L);

        assertEquals(reply, result);
    }

    @Test
    @DisplayName("获取回复详情 - 不存在")
    void testGetReply_notExist() {
        when(replyMapper.selectById(2L)).thenReturn(null);

        DiscussionReply result = replyService.getReply(2L);

        assertNull(result);
    }

    @Test
    @DisplayName("获取讨论下的所有回复 - 正常")
    void testGetRepliesByDiscussion_success() {
        List<DiscussionReply> replies = Arrays.asList(new DiscussionReply(), new DiscussionReply());
        when(replyMapper.findByDiscussionId(1L)).thenReturn(replies);

        List<DiscussionReply> result = replyService.getRepliesByDiscussion(1L);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("获取讨论下的所有回复 - 空列表")
    void testGetRepliesByDiscussion_empty() {
        when(replyMapper.findByDiscussionId(1L)).thenReturn(Collections.emptyList());

        List<DiscussionReply> result = replyService.getRepliesByDiscussion(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("获取用户的所有回复 - 正常")
    void testGetRepliesByUser_success() {
        List<DiscussionReply> replies = Arrays.asList(new DiscussionReply());
        when(replyMapper.findByUserId(2L)).thenReturn(replies);

        List<DiscussionReply> result = replyService.getRepliesByUser(2L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("获取用户的所有回复 - 空列表")
    void testGetRepliesByUser_empty() {
        when(replyMapper.findByUserId(2L)).thenReturn(Collections.emptyList());

        List<DiscussionReply> result = replyService.getRepliesByUser(2L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("获取顶层回复 - 正常")
    void testGetTopLevelReplies_success() {
        List<DiscussionReply> replies = Arrays.asList(new DiscussionReply());
        when(replyMapper.findTopLevelReplies(1L)).thenReturn(replies);

        List<DiscussionReply> result = replyService.getTopLevelReplies(1L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("获取顶层回复 - 空列表")
    void testGetTopLevelReplies_empty() {
        when(replyMapper.findTopLevelReplies(1L)).thenReturn(Collections.emptyList());

        List<DiscussionReply> result = replyService.getTopLevelReplies(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("获取子回复 - 正常")
    void testGetRepliesByParent_success() {
        List<DiscussionReply> replies = Arrays.asList(new DiscussionReply());
        when(replyMapper.findRepliesByParentId(3L)).thenReturn(replies);

        List<DiscussionReply> result = replyService.getRepliesByParent(3L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("获取子回复 - 空列表")
    void testGetRepliesByParent_empty() {
        when(replyMapper.findRepliesByParentId(3L)).thenReturn(Collections.emptyList());

        List<DiscussionReply> result = replyService.getRepliesByParent(3L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("获取教师回复 - 正常")
    void testGetTeacherReplies_success() {
        List<DiscussionReply> replies = Arrays.asList(new DiscussionReply());
        when(replyMapper.findTeacherReplies(1L)).thenReturn(replies);

        List<DiscussionReply> result = replyService.getTeacherReplies(1L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("获取教师回复 - 空列表")
    void testGetTeacherReplies_empty() {
        when(replyMapper.findTeacherReplies(1L)).thenReturn(Collections.emptyList());

        List<DiscussionReply> result = replyService.getTeacherReplies(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("统计讨论回复数 - 正常")
    void testCountRepliesByDiscussion_success() {
        when(replyMapper.countByDiscussionId(1L)).thenReturn(5);

        int count = replyService.countRepliesByDiscussion(1L);

        assertEquals(5, count);
    }

    @Test
    @DisplayName("统计讨论回复数 - 为0")
    void testCountRepliesByDiscussion_zero() {
        when(replyMapper.countByDiscussionId(1L)).thenReturn(0);

        int count = replyService.countRepliesByDiscussion(1L);


        assertEquals(0, count);
    }

    @Test
    @DisplayName("统计用户回复数 - 正常")
    void testCountRepliesByUser_success() {
        when(replyMapper.countByUserId(2L)).thenReturn(3);

        int count = replyService.countRepliesByUser(2L);

        assertEquals(3, count);
    }

    @Test
    @DisplayName("统计用户回复数 - 为0")
    void testCountRepliesByUser_zero() {
        when(replyMapper.countByUserId(2L)).thenReturn(0);

        int count = replyService.countRepliesByUser(2L);

        assertEquals(0, count);
    }

    @Test
    @DisplayName("删除讨论的所有回复 - 正常")
    void testDeleteRepliesByDiscussion_success() {
        DiscussionReply r1 = new DiscussionReply();
        r1.setId(1L);
        r1.setParentReplyId(null);
        r1.setDiscussionId(10L);

        DiscussionReply r2 = new DiscussionReply();
        r2.setId(2L);
        r2.setParentReplyId(1L);
        r2.setDiscussionId(10L);

        List<DiscussionReply> replies = Arrays.asList(r1, r2);
        when(replyMapper.findByDiscussionId(10L)).thenReturn(replies);
        replyService.deleteRepliesByDiscussion(10L);

        verify(replyMapper, times(2)).deleteById(anyLong());
        verify(discussionMapper, times(2)).decrementReplyCount(10L);
    }

    @Test
    @DisplayName("删除讨论的所有回复 - 无回复")
    void testDeleteRepliesByDiscussion_none() {
        when(replyMapper.findByDiscussionId(10L)).thenReturn(Collections.emptyList());

        replyService.deleteRepliesByDiscussion(10L);

        verify(replyMapper, never()).deleteById(anyLong());
        verify(discussionMapper, never()).decrementReplyCount(anyLong());
    }
}
