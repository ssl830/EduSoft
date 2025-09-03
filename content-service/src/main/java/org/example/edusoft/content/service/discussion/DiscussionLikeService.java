package org.example.edusoft.content.service.discussion;

import org.example.edusoft.content.entity.discussion.DiscussionLike;
import java.util.List;

/**
 * 讨论点赞服务接口
 */
public interface DiscussionLikeService {
    
    /**
     * 点赞讨论
     */
    DiscussionLike likeDiscussion(Long discussionId, String userId);
    
    /**
     * 取消点赞
     */
    void unlikeDiscussion(Long discussionId, String userId);
    
    /**
     * 检查是否已点赞
     */
    boolean hasLiked(Long discussionId, String userId);
    
    /**
     * 获取讨论的点赞列表
     */
    List<DiscussionLike> getDiscussionLikes(Long discussionId);
    
    /**
     * 获取用户的点赞列表
     */
    List<DiscussionLike> getUserLikes(String userId);
    
    /**
     * 获取讨论的点赞数量
     */
    int getLikeCount(Long discussionId);
    
    /**
     * 根据讨论ID获取点赞列表（别名方法）
     */
    List<DiscussionLike> getLikesByDiscussion(Long discussionId);
    
    /**
     * 根据用户ID获取点赞列表（别名方法）
     */
    List<DiscussionLike> getLikesByUser(String userId);
    
    /**
     * 统计讨论的点赞数量（别名方法）
     */
    int countLikesByDiscussion(Long discussionId);
    
    /**
     * 统计用户的点赞数量
     */
    int countLikesByUser(String userId);
}
