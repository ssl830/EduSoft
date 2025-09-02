package org.example.edusoft.content.service.discussion;

import org.example.edusoft.content.entity.discussion.Discussion;
import java.util.List;

public interface DiscussionService {
    
    /**
     * 创建讨论
     */
    Discussion createDiscussion(Discussion discussion);
    
    /**
     * 更新讨论
     */
    Discussion updateDiscussion(Discussion discussion);
    
    /**
     * 删除讨论
     */
    void deleteDiscussion(Long id);
    
    /**
     * 获取讨论详情
     */
    Discussion getDiscussion(Long id);
    
    /**
     * 增加浏览次数
     */
    void incrementViewCount(Long id);
    
    /**
     * 根据课程获取讨论列表
     */
    List<Discussion> getDiscussionsByCourse(Long courseId);
    
    /**
     * 根据班级获取讨论列表
     */
    List<Discussion> getDiscussionsByClass(Long classId);
    
    /**
     * 根据创建者获取讨论列表
     */
    List<Discussion> getDiscussionsByCreator(Long creatorId);
    
    /**
     * 根据课程和班级获取讨论列表
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
     * 统计课程下的讨论数量
     */
    Integer countDiscussionsByCourse(Long courseId);
    
    /**
     * 统计班级下的讨论数量
     */
    Integer countDiscussionsByClass(Long classId);
}
