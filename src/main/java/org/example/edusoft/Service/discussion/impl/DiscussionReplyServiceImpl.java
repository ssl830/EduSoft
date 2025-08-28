package org.example.edusoft.service.discussion.impl;

import org.example.edusoft.entity.discussion.DiscussionReply;
import org.example.edusoft.mapper.discussion.DiscussionMapper;
import org.example.edusoft.mapper.discussion.DiscussionReplyMapper;
import org.example.edusoft.service.discussion.DiscussionReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DiscussionReplyServiceImpl implements DiscussionReplyService {
    
    @Autowired
    private DiscussionReplyMapper replyMapper;
    
    @Autowired
    private DiscussionMapper discussionMapper;
    
    @Override
    @Transactional
    public DiscussionReply createReply(DiscussionReply reply) {
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        reply.setCreatedAt(now);
        reply.setUpdatedAt(now);
        
        // 如果是回复其他回复，验证父回复是否存在
        if (reply.getParentReplyId() != null) {
            DiscussionReply parentReply = replyMapper.selectById(reply.getParentReplyId());
            if (parentReply == null) {
                throw new IllegalArgumentException("Parent reply does not exist");
            }
            // 确保父回复属于同一个讨论
            if (!parentReply.getDiscussionId().equals(reply.getDiscussionId())) {
                throw new IllegalArgumentException("Parent reply does not belong to this discussion");
            }
        }
        
        replyMapper.insert(reply);
        // 更新讨论的回复数
        discussionMapper.incrementReplyCount(reply.getDiscussionId());
        return reply;
    }
    
    @Override
    @Transactional
    public DiscussionReply updateReply(DiscussionReply reply) {
        // 获取原始回复
        DiscussionReply existingReply = replyMapper.selectById(reply.getId());
        if (existingReply != null) {
            // 保留原始创建时间
            reply.setCreatedAt(existingReply.getCreatedAt());
            // 设置新的更新时间
            reply.setUpdatedAt(LocalDateTime.now());
        }
        replyMapper.updateById(reply);
        return reply;
    }
    
    @Override
    @Transactional
    public void deleteReply(Long id) {
        DiscussionReply reply = replyMapper.selectById(id);
        if (reply != null) {
            replyMapper.deleteById(id);
            // 更新讨论的回复数
            discussionMapper.decrementReplyCount(reply.getDiscussionId());
        }
    }
    
    @Override
    public DiscussionReply getReply(Long id) {
        return replyMapper.selectById(id);
    }
    
    @Override
    public List<DiscussionReply> getRepliesByDiscussion(Long discussionId) {
        return replyMapper.findByDiscussionId(discussionId);
    }
    
    @Override
    public List<DiscussionReply> getRepliesByUser(Long userId) {
        return replyMapper.findByUserId(userId);
    }
    
    @Override
    public List<DiscussionReply> getTopLevelReplies(Long discussionId) {
        return replyMapper.findTopLevelReplies(discussionId);
    }
    
    @Override
    public List<DiscussionReply> getRepliesByParent(Long parentReplyId) {
        return replyMapper.findRepliesByParentId(parentReplyId);
    }
    
    @Override
    public List<DiscussionReply> getTeacherReplies(Long discussionId) {
        return replyMapper.findTeacherReplies(discussionId);
    }
    
    @Override
    public int countRepliesByDiscussion(Long discussionId) {
        return replyMapper.countByDiscussionId(discussionId);
    }
    
    @Override
    public int countRepliesByUser(Long userId) {
        return replyMapper.countByUserId(userId);
    }
    
    @Override
    @Transactional
    public void deleteRepliesByDiscussion(Long discussionId) {
        // 先获取所有回复
        List<DiscussionReply> allReplies = replyMapper.findByDiscussionId(discussionId);
        
        // 按照父子关系排序，确保先删除子回复
        allReplies.sort((a, b) -> {
            // 如果a是b的父回复，a应该排在后面
            if (b.getParentReplyId() != null && b.getParentReplyId().equals(a.getId())) {
                return 1;
            }
            // 如果b是a的父回复，b应该排在后面
            if (a.getParentReplyId() != null && a.getParentReplyId().equals(b.getId())) {
                return -1;
            }
            return 0;
        });
        
        // 按顺序删除回复
        for (DiscussionReply reply : allReplies) {
            replyMapper.deleteById(reply.getId());
        }
        
        // 重置讨论的回复数
        int count = allReplies.size();
        for (int i = 0; i < count; i++) {
            discussionMapper.decrementReplyCount(discussionId);
        }
    }
} 