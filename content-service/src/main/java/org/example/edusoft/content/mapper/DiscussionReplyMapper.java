package org.example.edusoft.content.mapper;

import org.example.edusoft.content.entity.DiscussionReply;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DiscussionReplyMapper {
    
    @Select("SELECT * FROM discussion_reply WHERE id = #{id}")
    DiscussionReply findById(@Param("id") Long id);
    
    @Select("SELECT * FROM discussion_reply WHERE discussion_id = #{discussionId} AND status = 'active' ORDER BY created_at ASC")
    List<DiscussionReply> findByDiscussionId(@Param("discussionId") Long discussionId);
    
    @Select("SELECT * FROM discussion_reply WHERE user_id = #{userId} AND status = 'active' ORDER BY created_at DESC")
    List<DiscussionReply> findByUserId(@Param("userId") Long userId);
    
    @Select("SELECT * FROM discussion_reply WHERE parent_reply_id = #{parentReplyId} AND status = 'active' ORDER BY created_at ASC")
    List<DiscussionReply> findByParentReplyId(@Param("parentReplyId") Long parentReplyId);
    
    @Insert("INSERT INTO discussion_reply (discussion_id, user_id, user_num, content, parent_reply_id, " +
            "is_teacher_reply, like_count, status, created_at, updated_at) VALUES " +
            "(#{discussionId}, #{userId}, #{userNum}, #{content}, #{parentReplyId}, " +
            "#{isTeacherReply}, 0, 'active', NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(DiscussionReply reply);
    
    @Update("UPDATE discussion_reply SET content = #{content}, updated_at = NOW() WHERE id = #{id}")
    void update(DiscussionReply reply);
    
    @Update("UPDATE discussion_reply SET like_count = like_count + 1 WHERE id = #{id}")
    void incrementLikeCount(@Param("id") Long id);
    
    @Update("UPDATE discussion_reply SET status = 'deleted', updated_at = NOW() WHERE id = #{id}")
    void deleteById(@Param("id") Long id);
    
    @Update("UPDATE discussion_reply SET is_teacher_reply = true, updated_at = NOW() WHERE id = #{id}")
    void markAsTeacherReply(@Param("id") Long id);
}
