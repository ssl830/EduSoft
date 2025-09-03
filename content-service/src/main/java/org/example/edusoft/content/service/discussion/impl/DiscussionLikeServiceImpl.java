package org.example.edusoft.content.service.discussion.impl;

import org.example.edusoft.content.entity.discussion.DiscussionLike;
import org.example.edusoft.content.mapper.discussion.DiscussionLikeMapper;
import org.example.edusoft.content.service.discussion.DiscussionLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class DiscussionLikeServiceImpl implements DiscussionLikeService {
    
    @Autowired
    private DiscussionLikeMapper discussionLikeMapper;
    
    @Override
    public DiscussionLike likeDiscussion(Long discussionId, Long userId) {
        if (discussionId == null) {
            throw new IllegalArgumentException("讨论ID不能为空");
        }
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        // 检查是否已经点赞
        Integer existingCount = discussionLikeMapper.countByDiscussionAndUser(discussionId, userId);
        if (existingCount > 0) {
            throw new IllegalArgumentException("您已经点赞过这个讨论了");
        }
        
        DiscussionLike like = new DiscussionLike();
        like.setDiscussionId(discussionId);
        like.setUserId(userId);
        
        discussionLikeMapper.insert(like);
        return like;
    }
    
    @Override
    public void unlikeDiscussion(Long discussionId, Long userId) {
        if (discussionId == null) {
            throw new IllegalArgumentException("讨论ID不能为空");
        }
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        int deleted = discussionLikeMapper.deleteByDiscussionAndUser(discussionId, userId);
        if (deleted == 0) {
            throw new IllegalArgumentException("您还没有点赞过这个讨论");
        }
    }
    
    @Override
    public List<DiscussionLike> getLikesByDiscussion(Long discussionId) {
        if (discussionId == null) {
            throw new IllegalArgumentException("讨论ID不能为空");
        }
        
        return discussionLikeMapper.selectByDiscussionId(discussionId);
    }
    
    @Override
    public List<DiscussionLike> getLikesByUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        return discussionLikeMapper.selectByUserId(userId);
    }
    
    @Override
    public Integer countLikesByDiscussion(Long discussionId) {
        if (discussionId == null) {
            throw new IllegalArgumentException("讨论ID不能为空");
        }
        
        return discussionLikeMapper.countByDiscussionId(discussionId);
    }
    
    @Override
    public Integer countLikesByUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        return discussionLikeMapper.countByUserId(userId);
    }
    
    @Override
    public Boolean hasLiked(Long discussionId, Long userId) {
        if (discussionId == null) {
            throw new IllegalArgumentException("讨论ID不能为空");
        }
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        Integer count = discussionLikeMapper.countByDiscussionAndUser(discussionId, userId);
        return count > 0;
    }
}
