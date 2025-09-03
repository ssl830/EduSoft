package org.example.edusoft.content.service.discussion;

import org.example.edusoft.content.entity.discussion.DiscussionLike;
import java.util.List;

public interface DiscussionLikeService {
    
    /**
     * 点赞讨论
     */
    DiscussionLike likeDiscussion(Long discussionId, Long userId);
    
    /**
     * 取消点赞
     */
    void unlikeDiscussion(Long discussionId, Long userId);
    
    /**
     * 获取讨论的点赞列表
     */
    List<DiscussionLike> getLikesByDiscussion(Long discussionId);
    
    /**
     * 获取用户的点赞列表
     */
    List<DiscussionLike> getLikesByUser(Long userId);
    
    /**
     * 获取讨论的点赞数
     */
    Integer countLikesByDiscussion(Long discussionId);
    
    /**
     * 获取用户的点赞数
     */
    Integer countLikesByUser(Long userId);
    
    /**
     * 检查用户是否已点赞
     */
    Boolean hasLiked(Long discussionId, Long userId);
}
