package org.example.edusoft.content.service.discussion;

import org.example.edusoft.content.entity.discussion.Discussion;
import java.util.List;
import java.util.Map;

/**
 * 讨论服务接口
 */
public interface DiscussionService {
    
    /**
     * 创建讨论
     */
    Discussion createDiscussion(Discussion discussion);
    
    /**
     * 获取讨论列表
     */
    List<Discussion> getDiscussions(Long courseId, Long classId, String type, int page, int size);
    
    /**
     * 根据ID获取讨论
     */
    Discussion getDiscussionById(Long id);
    
    /**
     * 更新讨论
     */
    Discussion updateDiscussion(Discussion discussion);
    
    /**
     * 删除讨论
     */
    void deleteDiscussion(Long id);
    
    /**
     * 搜索讨论
     */
    List<Discussion> searchDiscussions(String keyword, Long courseId, Long classId);
    
    /**
     * 增加浏览次数
     */
    void incrementViewCount(Long id);
    
    /**
     * 增加回复次数
     */
    void incrementReplyCount(Long id);
    
    /**
     * 增加点赞次数
     */
    void incrementLikeCount(Long id);
    
    /**
     * 减少点赞次数
     */
    void decrementLikeCount(Long id);
    
    /**
     * 根据ID获取讨论（别名方法）
     */
    Discussion getDiscussion(Long id);
    
    /**
     * 根据课程ID获取讨论列表
     */
    List<Discussion> getDiscussionsByCourse(Long courseId);
    
    /**
     * 根据班级ID获取讨论列表
     */
    List<Discussion> getDiscussionsByClass(Long classId);
    
    /**
     * 根据创建者ID获取讨论列表
     */
    List<Discussion> getDiscussionsByCreator(Long creatorId);
    
    /**
     * 根据课程ID和班级ID获取讨论列表
     */
    List<Discussion> getDiscussionsByCourseAndClass(Long courseId, Long classId);
    
    /**
     * 更新置顶状态
     */
    void updatePinnedStatus(Long id, Boolean isPinned);
    
    /**
     * 更新关闭状态
     */
    void updateClosedStatus(Long id, Boolean isClosed);
    
    /**
     * 统计课程讨论数量
     */
    int countDiscussionsByCourse(Long courseId);
    
    /**
     * 统计班级讨论数量
     */
    int countDiscussionsByClass(Long classId);
}
