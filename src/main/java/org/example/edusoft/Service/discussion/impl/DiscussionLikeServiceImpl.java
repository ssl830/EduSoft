package org.example.edusoft.service.discussion.impl;

import org.example.edusoft.entity.discussion.DiscussionLike;
import org.example.edusoft.mapper.discussion.DiscussionLikeMapper;
import org.example.edusoft.service.discussion.DiscussionLikeService;
import org.example.edusoft.service.discussion.DiscussionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class DiscussionLikeServiceImpl implements DiscussionLikeService {
    
    @Autowired
    private DiscussionLikeMapper discussionLikeMapper;
    
    @Autowired
    private DiscussionService discussionService;
    
    @Override
    @Transactional
    public DiscussionLike likeDiscussion(Long discussionId, Long userId) {
        // 检查讨论是否存在
        if (discussionService.getDiscussion(discussionId) == null) {
            throw new IllegalArgumentException("讨论不存在");
        }
        
        // 检查是否已经点赞
        if (hasLiked(discussionId, userId)) {
            return discussionLikeMapper.findByDiscussionAndUser(discussionId, userId);
        }
        
        // 创建新的点赞记录
        DiscussionLike like = new DiscussionLike();
        like.setDiscussionId(discussionId);
        like.setUserId(userId);
        like.setCreatedAt(LocalDateTime.now());
        
        discussionLikeMapper.insert(like);
        return like;
    }
    
    @Override
    @Transactional
    public void unlikeDiscussion(Long discussionId, Long userId) {
        discussionLikeMapper.deleteByDiscussionAndUser(discussionId, userId);
    }
    
    @Override
    public List<DiscussionLike> getLikesByDiscussion(Long discussionId) {
        return discussionLikeMapper.findByDiscussionId(discussionId);
    }
    
    @Override
    public List<DiscussionLike> getLikesByUser(Long userId) {
        return discussionLikeMapper.findByUserId(userId);
    }
    
    @Override
    public int countLikesByDiscussion(Long discussionId) {
        return discussionLikeMapper.countByDiscussionId(discussionId);
    }
    
    @Override
    public int countLikesByUser(Long userId) {
        return discussionLikeMapper.countByUserId(userId);
    }
    
    @Override
    public boolean hasLiked(Long discussionId, Long userId) {
        return discussionLikeMapper.findByDiscussionAndUser(discussionId, userId) != null;
    }
} 