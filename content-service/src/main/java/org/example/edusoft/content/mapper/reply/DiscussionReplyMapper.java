package org.example.edusoft.content.mapper.reply;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.content.entity.reply.DiscussionReply;
import org.example.edusoft.content.dto.reply.DiscussionReplyDTO;
import java.util.List;

@Mapper
public interface DiscussionReplyMapper {
    
    @Insert("INSERT INTO discussionreply (discussion_id, parent_reply_id, user_id, user_num, content, created_at, updated_at) " +
            "VALUES (#{discussionId}, #{parentReplyId}, #{userId}, #{userNum}, #{content}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DiscussionReply reply);
        
    @Select("SELECT id, discussion_id AS discussionId, parent_reply_id AS parentReplyId, user_id AS userId, user_num AS userNum, content, created_at AS createdAt, updated_at AS updatedAt FROM discussionreply WHERE id = #{id}")
    DiscussionReply selectById(@Param("id") Long id);
        
    @Select("SELECT id, discussion_id AS discussionId, parent_reply_id AS parentReplyId, user_id AS userId, user_num AS userNum, content, created_at AS createdAt, updated_at AS updatedAt FROM discussionreply WHERE discussion_id = #{discussionId} ORDER BY created_at ASC")
    List<DiscussionReplyDTO> selectByDiscussionId(@Param("discussionId") Long discussionId);
        
    @Select("SELECT id, discussion_id AS discussionId, parent_reply_id AS parentReplyId, user_id AS creatorId, user_num AS creatorName, content, NULL AS likeCount, NULL AS isLiked, created_at AS createdAt, updated_at AS updatedAt, NULL AS isDeleted FROM discussionreply WHERE parent_reply_id = #{parentReplyId} ORDER BY created_at ASC")
    List<DiscussionReplyDTO> selectByParentReplyId(@Param("parentReplyId") Long parentReplyId);
        
    @Update("UPDATE discussionreply SET content = #{content}, updated_at = NOW() WHERE id = #{id}")
    int updateContent(@Param("id") Long id, @Param("content") String content, @Param("updatedAt") String updatedAt);
        
    @Update("DELETE FROM discussionreply WHERE id = #{id}")
    int softDelete(@Param("id") Long id, @Param("updatedAt") String updatedAt);
        
    @Update("UPDATE discussionreply SET updated_at = NOW() WHERE id = #{id}")
    int incrementLikeCount(@Param("id") Long id);
        
    @Update("UPDATE discussionreply SET updated_at = NOW() WHERE id = #{id}")
    int decrementLikeCount(@Param("id") Long id);
        
    @Select("SELECT id, discussion_id AS discussionId, parent_reply_id AS parentReplyId, user_id AS creatorId, user_num AS creatorName, content, NULL AS likeCount, NULL AS isLiked, created_at AS createdAt, updated_at AS updatedAt, NULL AS isDeleted FROM discussionreply WHERE user_id = #{creatorId} ORDER BY created_at DESC")
    List<DiscussionReplyDTO> selectByCreatorId(@Param("creatorId") Long creatorId);
        
    @Select("SELECT id, discussion_id AS discussionId, parent_reply_id AS parentReplyId, user_id AS creatorId, user_num AS creatorName, content, NULL AS likeCount, NULL AS isLiked, created_at AS createdAt, updated_at AS updatedAt, NULL AS isDeleted FROM discussionreply WHERE content LIKE CONCAT('%', #{keyword}, '%') ORDER BY created_at DESC")
    List<DiscussionReplyDTO> searchByKeyword(@Param("keyword") String keyword);
}
