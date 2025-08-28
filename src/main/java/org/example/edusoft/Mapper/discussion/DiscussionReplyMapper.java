package org.example.edusoft.mapper.discussion;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.discussion.DiscussionReply;
import java.util.List;

@Mapper
public interface DiscussionReplyMapper extends BaseMapper<DiscussionReply> {
    
    @Select("SELECT * FROM discussionreply WHERE discussion_id = #{discussionId} " +
            "ORDER BY parent_reply_id ASC, created_at ASC")
    List<DiscussionReply> findByDiscussionId(@Param("discussionId") Long discussionId);
    
    @Select("SELECT * FROM discussionreply WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<DiscussionReply> findByUserId(@Param("userId") Long userId);
    
    @Select("SELECT * FROM discussionreply WHERE discussion_id = #{discussionId} " +
            "AND parent_reply_id IS NULL ORDER BY created_at ASC")
    List<DiscussionReply> findTopLevelReplies(@Param("discussionId") Long discussionId);
    
    @Select("SELECT * FROM discussionreply WHERE parent_reply_id = #{parentReplyId} " +
            "ORDER BY created_at ASC")
    List<DiscussionReply> findRepliesByParentId(@Param("parentReplyId") Long parentReplyId);
    
    @Select("SELECT COUNT(*) FROM discussionreply WHERE discussion_id = #{discussionId}")
    int countByDiscussionId(@Param("discussionId") Long discussionId);
    
    @Select("SELECT COUNT(*) FROM discussionreply WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);
    
    @Delete("DELETE FROM discussionreply WHERE discussion_id = #{discussionId}")
    int deleteByDiscussionId(@Param("discussionId") Long discussionId);
    
    @Select("SELECT * FROM discussionreply WHERE discussion_id = #{discussionId} " +
            "AND is_teacher_reply = true ORDER BY created_at ASC")
    List<DiscussionReply> findTeacherReplies(@Param("discussionId") Long discussionId);
} 