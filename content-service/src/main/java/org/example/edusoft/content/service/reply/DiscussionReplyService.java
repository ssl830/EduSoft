package org.example.edusoft.content.service.reply;

import org.example.edusoft.content.dto.reply.DiscussionReplyDTO;
import org.example.edusoft.content.entity.reply.DiscussionReply;
import java.util.List;

public interface DiscussionReplyService {
    
    /**
     * 创建回复
     */
    DiscussionReply createReply(Long discussionId, Long parentReplyId, Long creatorId, String content, String userNum);
    
    /**
     * 获取回复详情
     */
    DiscussionReply getReply(Long id);
    
    /**
     * 获取讨论的所有回复
     */
    List<DiscussionReplyDTO> getDiscussionReplies(Long discussionId);
    
    /**
     * 获取回复的子回复
     */
    List<DiscussionReplyDTO> getChildReplies(Long parentReplyId);
    
    /**
     * 更新回复
     */
    DiscussionReply updateReply(Long id, String content);
    
    /**
     * 删除回复
     */
    boolean deleteReply(Long id);
    
    /**
     * 点赞回复
     */
    boolean likeReply(Long id);
    
    /**
     * 取消点赞
     */
    boolean unlikeReply(Long id);
    
    /**
     * 获取用户的回复列表
     */
    List<DiscussionReplyDTO> getUserReplies(Long userId);
    
    /**
     * 搜索回复
     */
    List<DiscussionReplyDTO> searchReplies(String keyword);
}
