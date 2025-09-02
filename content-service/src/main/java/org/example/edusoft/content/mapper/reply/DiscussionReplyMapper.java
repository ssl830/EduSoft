package org.example.edusoft.content.mapper.reply;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.content.entity.reply.DiscussionReply;
import org.example.edusoft.content.dto.reply.DiscussionReplyDTO;
import java.util.List;

@Mapper
public interface DiscussionReplyMapper {
    
    @Insert("INSERT INTO discussion_replies (discussion_id, parent_reply_id, creator_id, creator_name, content, like_count, created_at, updated_at, is_deleted) " +
            "VALUES (#{discussionId}, #{parentReplyId}, #{creatorId}, #{creatorName}, #{content}, #{likeCount}, #{createdAt}, #{updatedAt}, #{isDeleted})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DiscussionReply reply);
    
    @Select("SELECT * FROM discussion_replies WHERE id = #{id} AND is_deleted = FALSE")
    DiscussionReply selectById(@Param("id") Long id);
    
    @Select("SELECT * FROM discussion_replies WHERE discussion_id = #{discussionId} AND is_deleted = FALSE ORDER BY created_at ASC")
    List<DiscussionReplyDTO> selectByDiscussionId(@Param("discussionId") Long discussionId);
    
    @Select("SELECT * FROM discussion_replies WHERE parent_reply_id = #{parentReplyId} AND is_deleted = FALSE ORDER BY created_at ASC")
    List<DiscussionReplyDTO> selectByParentReplyId(@Param("parentReplyId") Long parentReplyId);
    
    @Update("UPDATE discussion_replies SET content = #{content}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateContent(@Param("id") Long id, @Param("content") String content, @Param("updatedAt") String updatedAt);
    
    @Update("UPDATE discussion_replies SET is_deleted = TRUE, updated_at = #{updatedAt} WHERE id = #{id}")
    int softDelete(@Param("id") Long id, @Param("updatedAt") String updatedAt);
    
    @Update("UPDATE discussion_replies SET like_count = like_count + 1 WHERE id = #{id}")
    int incrementLikeCount(@Param("id") Long id);
    
    @Update("UPDATE discussion_replies SET like_count = GREATEST(like_count - 1, 0) WHERE id = #{id}")
    int decrementLikeCount(@Param("id") Long id);
    
    @Select("SELECT * FROM discussion_replies WHERE creator_id = #{creatorId} AND is_deleted = FALSE ORDER BY created_at DESC")
    List<DiscussionReplyDTO> selectByCreatorId(@Param("creatorId") Long creatorId);
    
    @Select("SELECT * FROM discussion_replies WHERE content LIKE CONCAT('%', #{keyword}, '%') AND is_deleted = FALSE ORDER BY created_at DESC")
    List<DiscussionReplyDTO> searchByKeyword(@Param("keyword") String keyword);
}
