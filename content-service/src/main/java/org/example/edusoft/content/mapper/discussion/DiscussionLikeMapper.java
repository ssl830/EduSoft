package org.example.edusoft.content.mapper.discussion;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.content.entity.discussion.DiscussionLike;
import java.util.List;

@Mapper
public interface DiscussionLikeMapper {
    
    @Insert("INSERT INTO discussion_likes (discussion_id, user_id, created_at) VALUES (#{discussionId}, #{userId}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DiscussionLike discussionLike);
    
    @Delete("DELETE FROM discussion_likes WHERE discussion_id = #{discussionId} AND user_id = #{userId}")
    int deleteByDiscussionAndUser(@Param("discussionId") Long discussionId, @Param("userId") Long userId);
    
    @Select("SELECT * FROM discussion_likes WHERE discussion_id = #{discussionId}")
    List<DiscussionLike> selectByDiscussionId(Long discussionId);
    
    @Select("SELECT * FROM discussion_likes WHERE user_id = #{userId}")
    List<DiscussionLike> selectByUserId(Long userId);
    
    @Select("SELECT COUNT(*) FROM discussion_likes WHERE discussion_id = #{discussionId}")
    Integer countByDiscussionId(Long discussionId);
    
    @Select("SELECT COUNT(*) FROM discussion_likes WHERE user_id = #{userId}")
    Integer countByUserId(Long userId);
    
    @Select("SELECT COUNT(*) FROM discussion_likes WHERE discussion_id = #{discussionId} AND user_id = #{userId}")
    Integer countByDiscussionAndUser(@Param("discussionId") Long discussionId, @Param("userId") Long userId);
}
