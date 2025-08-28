package org.example.edusoft.mapper.discussion;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.discussion.DiscussionLike;
import java.util.List;

@Mapper
public interface DiscussionLikeMapper extends BaseMapper<DiscussionLike> {
    
    @Select("SELECT * FROM discussionlike WHERE discussion_id = #{discussionId}")
    List<DiscussionLike> findByDiscussionId(@Param("discussionId") Long discussionId);
    
    @Select("SELECT * FROM discussionlike WHERE user_id = #{userId}")
    List<DiscussionLike> findByUserId(@Param("userId") Long userId);
    
    @Select("SELECT COUNT(*) FROM discussionlike WHERE discussion_id = #{discussionId}")
    int countByDiscussionId(@Param("discussionId") Long discussionId);
    
    @Select("SELECT COUNT(*) FROM discussionlike WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);
    
    @Select("SELECT * FROM discussionlike WHERE discussion_id = #{discussionId} AND user_id = #{userId}")
    DiscussionLike findByDiscussionAndUser(@Param("discussionId") Long discussionId, @Param("userId") Long userId);
    
    @Delete("DELETE FROM discussionlike WHERE discussion_id = #{discussionId} AND user_id = #{userId}")
    int deleteByDiscussionAndUser(@Param("discussionId") Long discussionId, @Param("userId") Long userId);
} 