package org.example.edusoft.content.service.reply.impl;

import org.example.edusoft.content.dto.reply.DiscussionReplyDTO;
import org.example.edusoft.content.entity.reply.DiscussionReply;
import org.example.edusoft.content.mapper.reply.DiscussionReplyMapper;
import org.example.edusoft.content.service.reply.DiscussionReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DiscussionReplyServiceImpl implements DiscussionReplyService {
    
    @Autowired
    private DiscussionReplyMapper discussionReplyMapper;
    
    @Override
    public DiscussionReply createReply(Long discussionId, Long parentReplyId, Long creatorId, String content) {
        DiscussionReply reply = new DiscussionReply();
        reply.setDiscussionId(discussionId);
        reply.setParentReplyId(parentReplyId);
        reply.setCreatorId(creatorId);
        reply.setContent(content);
        reply.setLikeCount(0);
        reply.setCreatedAt(LocalDateTime.now());
        reply.setUpdatedAt(LocalDateTime.now());
        reply.setIsDeleted(false);
        
        discussionReplyMapper.insert(reply);
        return reply;
    }
    
    @Override
    public DiscussionReply getReply(Long id) {
        return discussionReplyMapper.selectById(id);
    }
    
    @Override
    public List<DiscussionReplyDTO> getDiscussionReplies(Long discussionId) {
        return discussionReplyMapper.selectByDiscussionId(discussionId);
    }
    
    @Override
    public List<DiscussionReplyDTO> getChildReplies(Long parentReplyId) {
        return discussionReplyMapper.selectByParentReplyId(parentReplyId);
    }
    
    @Override
    public DiscussionReply updateReply(Long id, String content) {
        DiscussionReply reply = getReply(id);
        if (reply != null) {
            discussionReplyMapper.updateContent(id, content, LocalDateTime.now().toString());
            reply.setContent(content);
            reply.setUpdatedAt(LocalDateTime.now());
        }
        return reply;
    }
    
    @Override
    public boolean deleteReply(Long id) {
        return discussionReplyMapper.softDelete(id, LocalDateTime.now().toString()) > 0;
    }
    
    @Override
    public boolean likeReply(Long id) {
        return discussionReplyMapper.incrementLikeCount(id) > 0;
    }
    
    @Override
    public boolean unlikeReply(Long id) {
        return discussionReplyMapper.decrementLikeCount(id) > 0;
    }
    
    @Override
    public List<DiscussionReplyDTO> getUserReplies(Long userId) {
        return discussionReplyMapper.selectByCreatorId(userId);
    }
    
    @Override
    public List<DiscussionReplyDTO> searchReplies(String keyword) {
        return discussionReplyMapper.searchByKeyword(keyword);
    }
}
