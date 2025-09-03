package org.example.edusoft.content.service.reply.impl;

import org.example.edusoft.content.entity.reply.DiscussionReply;
import org.example.edusoft.content.service.reply.DiscussionReplyService;
import org.example.edusoft.content.dto.reply.DiscussionReplyDTO;
import org.example.edusoft.content.mapper.reply.DiscussionReplyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 讨论回复服务实现类
 */
@Service
public class DiscussionReplyServiceImpl implements DiscussionReplyService {
    
    @Autowired
    private DiscussionReplyMapper replyMapper;
    
    @Override
    @Transactional
    public DiscussionReply createReply(Long discussionId, Long parentReplyId, Long creatorId, String content, String userNum) {
        DiscussionReply reply = new DiscussionReply();
        reply.setDiscussionId(discussionId);
        reply.setParentReplyId(parentReplyId);
        reply.setUserId(creatorId);
        reply.setUserNum(userNum);
        reply.setContent(content);
        reply.setIsTeacherReply(userNum.startsWith("T"));
        
        replyMapper.insert(reply);
        return reply;
    }
    
    @Override
    public DiscussionReply getReply(Long id) {
        return replyMapper.selectById(id);
    }
    
    @Override
    public List<DiscussionReplyDTO> getDiscussionReplies(Long discussionId) {
        return replyMapper.selectByDiscussionId(discussionId);
    }
    
    @Override
    public List<DiscussionReplyDTO> getChildReplies(Long parentReplyId) {
        return replyMapper.selectByParentReplyId(parentReplyId);
    }
    
    @Override
    @Transactional
    public DiscussionReply updateReply(Long id, String content) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String updatedAt = LocalDateTime.now().format(formatter);
        replyMapper.updateContent(id, content, updatedAt);
        return replyMapper.selectById(id);
    }
    
    @Override
    @Transactional
    public boolean deleteReply(Long id) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String updatedAt = LocalDateTime.now().format(formatter);
        return replyMapper.softDelete(id, updatedAt) > 0;
    }
    
    @Override
    @Transactional
    public boolean likeReply(Long id) {
        return replyMapper.incrementLikeCount(id) > 0;
    }
    
    @Override
    @Transactional
    public boolean unlikeReply(Long id) {
        return replyMapper.decrementLikeCount(id) > 0;
    }
    
    @Override
    public List<DiscussionReplyDTO> getUserReplies(Long userId) {
        return replyMapper.selectByCreatorId(userId);
    }
    
    @Override
    public List<DiscussionReplyDTO> searchReplies(String keyword) {
        return replyMapper.searchByKeyword(keyword);
    }
}
