package org.example.edusoft.content.service.discussion.impl;

import org.example.edusoft.content.entity.discussion.Discussion;
import org.example.edusoft.content.mapper.discussion.DiscussionMapper;
import org.example.edusoft.content.service.discussion.DiscussionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class DiscussionServiceImpl implements DiscussionService {
    
    @Autowired
    private DiscussionMapper discussionMapper;
    
    @Override
    public Discussion createDiscussion(Discussion discussion) {
        if (discussion.getTitle() == null || discussion.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("讨论标题不能为空");
        }
        if (discussion.getContent() == null || discussion.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("讨论内容不能为空");
        }
        if (discussion.getCourseId() == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }
        if (discussion.getClassId() == null) {
            throw new IllegalArgumentException("班级ID不能为空");
        }
        if (discussion.getCreatorId() == null) {
            throw new IllegalArgumentException("创建者ID不能为空");
        }
        
        discussionMapper.insert(discussion);
        return discussion;
    }
    
    @Override
    public Discussion updateDiscussion(Discussion discussion) {
        if (discussion.getId() == null) {
            throw new IllegalArgumentException("讨论ID不能为空");
        }
        if (discussion.getTitle() == null || discussion.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("讨论标题不能为空");
        }
        if (discussion.getContent() == null || discussion.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("讨论内容不能为空");
        }
        
        int updated = discussionMapper.update(discussion);
        if (updated == 0) {
            throw new IllegalArgumentException("讨论不存在");
        }
        return discussion;
    }
    
    @Override
    public void deleteDiscussion(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("讨论ID不能为空");
        }
        
        int deleted = discussionMapper.deleteById(id);
        if (deleted == 0) {
            throw new IllegalArgumentException("讨论不存在");
        }
    }
    
    @Override
    public Discussion getDiscussion(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("讨论ID不能为空");
        }
        
        return discussionMapper.selectById(id);
    }
    
    @Override
    public void incrementViewCount(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("讨论ID不能为空");
        }
        
        discussionMapper.incrementViewCount(id);
    }
    
    @Override
    public List<Discussion> getDiscussionsByCourse(Long courseId) {
        if (courseId == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }
        
        return discussionMapper.selectByCourseId(courseId);
    }
    
    @Override
    public List<Discussion> getDiscussionsByClass(Long classId) {
        if (classId == null) {
            throw new IllegalArgumentException("班级ID不能为空");
        }
        
        return discussionMapper.selectByClassId(classId);
    }
    
    @Override
    public List<Discussion> getDiscussionsByCreator(Long creatorId) {
        if (creatorId == null) {
            throw new IllegalArgumentException("创建者ID不能为空");
        }
        
        return discussionMapper.selectByCreatorId(creatorId);
    }
    
    @Override
    public List<Discussion> getDiscussionsByCourseAndClass(Long courseId, Long classId) {
        if (courseId == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }
        if (classId == null) {
            throw new IllegalArgumentException("班级ID不能为空");
        }
        
        return discussionMapper.selectByCourseAndClass(courseId, classId);
    }
    
    @Override
    public void updatePinnedStatus(Long id, Boolean isPinned) {
        if (id == null) {
            throw new IllegalArgumentException("讨论ID不能为空");
        }
        if (isPinned == null) {
            throw new IllegalArgumentException("置顶状态不能为空");
        }
        
        int updated = discussionMapper.updatePinnedStatus(id, isPinned);
        if (updated == 0) {
            throw new IllegalArgumentException("讨论不存在");
        }
    }
    
    @Override
    public void updateClosedStatus(Long id, Boolean isClosed) {
        if (id == null) {
            throw new IllegalArgumentException("讨论ID不能为空");
        }
        if (isClosed == null) {
            throw new IllegalArgumentException("关闭状态不能为空");
        }
        
        int updated = discussionMapper.updateClosedStatus(id, isClosed);
        if (updated == 0) {
            throw new IllegalArgumentException("讨论不存在");
        }
    }
    
    @Override
    public Integer countDiscussionsByCourse(Long courseId) {
        if (courseId == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }
        
        Integer count = discussionMapper.countByCourseId(courseId);
        return count != null ? count : 0;
    }
    
    @Override
    public Integer countDiscussionsByClass(Long classId) {
        if (classId == null) {
            throw new IllegalArgumentException("班级ID不能为空");
        }
        
        Integer count = discussionMapper.countByClassId(classId);
        return count != null ? count : 0;
    }
}
