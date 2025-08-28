package org.example.edusoft.service.discussion;

import org.example.edusoft.entity.discussion.Discussion;
import java.util.List;

public interface DiscussionService {
    // 创建讨论
    Discussion createDiscussion(Discussion discussion);
    
    // 更新讨论
    Discussion updateDiscussion(Discussion discussion);
    
    // 删除讨论
    void deleteDiscussion(Long id);
    
    // 获取讨论详情
    Discussion getDiscussion(Long id);
    
    // 获取课程下的讨论列表
    List<Discussion> getDiscussionsByCourse(Long courseId);
    
    // 获取班级下的讨论列表
    List<Discussion> getDiscussionsByClass(Long classId);
    
    // 获取用户创建的讨论列表
    List<Discussion> getDiscussionsByCreator(Long creatorId);
    
    // 获取特定课程和班级的讨论列表
    List<Discussion> getDiscussionsByCourseAndClass(Long courseId, Long classId);
    
    // 增加浏览次数
    void incrementViewCount(Long id);
    
    // 更新置顶状态
    void updatePinnedStatus(Long id, Boolean isPinned);
    
    // 更新关闭状态
    void updateClosedStatus(Long id, Boolean isClosed);
    
    // 统计课程讨论数
    int countDiscussionsByCourse(Long courseId);
    
    // 统计班级讨论数
    int countDiscussionsByClass(Long classId);
} 