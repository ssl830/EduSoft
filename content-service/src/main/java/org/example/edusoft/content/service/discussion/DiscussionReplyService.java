package org.example.edusoft.service.discussion;

import org.example.edusoft.entity.discussion.DiscussionReply;
import java.util.List;

public interface DiscussionReplyService {
    // 创建回复
    DiscussionReply createReply(DiscussionReply reply);
    
    // 更新回复
    DiscussionReply updateReply(DiscussionReply reply);
    
    // 删除回复
    void deleteReply(Long id);
    
    // 获取回复详情
    DiscussionReply getReply(Long id);
    
    // 获取讨论下的所有回复
    List<DiscussionReply> getRepliesByDiscussion(Long discussionId);
    
    // 获取用户的所有回复
    List<DiscussionReply> getRepliesByUser(Long userId);
    
    // 获取讨论的顶层回复
    List<DiscussionReply> getTopLevelReplies(Long discussionId);
    
    // 获取特定回复的子回复
    List<DiscussionReply> getRepliesByParent(Long parentReplyId);
    
    // 获取教师回复
    List<DiscussionReply> getTeacherReplies(Long discussionId);
    
    // 统计讨论的回复数
    int countRepliesByDiscussion(Long discussionId);
    
    // 统计用户的回复数
    int countRepliesByUser(Long userId);
    
    // 删除讨论的所有回复
    void deleteRepliesByDiscussion(Long discussionId);
} 