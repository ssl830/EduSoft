package org.example.edusoft.service.discussion.impl;

import org.example.edusoft.entity.discussion.Discussion;
import org.example.edusoft.mapper.discussion.DiscussionMapper;
import org.example.edusoft.service.discussion.DiscussionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class DiscussionServiceImpl implements DiscussionService {

    @Autowired
    private DiscussionMapper discussionMapper;

    @Override
    @Transactional
    public Discussion createDiscussion(Discussion discussion) {
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        discussion.setCreatedAt(now);
        discussion.setUpdatedAt(now);

        discussionMapper.insert(discussion);
        return discussion;
    }

    @Override
    @Transactional
    public Discussion updateDiscussion(Discussion discussion) {
        // 获取原有讨论信息
        Discussion existingDiscussion = discussionMapper.selectById(discussion.getId());
        if (existingDiscussion != null) {
            // 保持原有的字段值
            discussion.setCreatedAt(existingDiscussion.getCreatedAt());
            discussion.setCreatorNum(existingDiscussion.getCreatorNum());
            discussion.setCreatorId(existingDiscussion.getCreatorId());
            discussion.setCourseId(existingDiscussion.getCourseId());
            discussion.setClassId(existingDiscussion.getClassId());
            discussion.setViewCount(existingDiscussion.getViewCount());
            discussion.setReplyCount(existingDiscussion.getReplyCount());
            discussion.setIsPinned(existingDiscussion.getIsPinned());
            discussion.setIsClosed(existingDiscussion.getIsClosed());
        }

        // 设置更新时间
        discussion.setUpdatedAt(LocalDateTime.now());

        discussionMapper.updateById(discussion);
        return discussion;
    }

    @Override
    @Transactional
    public void deleteDiscussion(Long id) {
        if (id == null) return;
        discussionMapper.deleteById(id);
    }

    @Override
    public Discussion getDiscussion(Long id) {
        return discussionMapper.selectById(id);
    }

    @Override
    public List<Discussion> getDiscussionsByCourse(Long courseId) {
        return discussionMapper.findByCourseId(courseId);
    }

    @Override
    public List<Discussion> getDiscussionsByClass(Long classId) {
        return discussionMapper.findByClassId(classId);
    }

    @Override
    public List<Discussion> getDiscussionsByCreator(Long creatorId) {
        return discussionMapper.findByCreatorId(creatorId);
    }

    @Override
    public List<Discussion> getDiscussionsByCourseAndClass(Long courseId, Long classId) {
        return discussionMapper.findByCourseAndClass(courseId, classId);
    }

    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        if (id == null) return;
        discussionMapper.incrementViewCount(id);
    }

    @Override
    @Transactional
    public void updatePinnedStatus(Long id, Boolean isPinned) {
        if (id == null) return;
        discussionMapper.updatePinnedStatus(id, isPinned);
    }

    @Override
    @Transactional
    public void updateClosedStatus(Long id, Boolean isClosed) {
        if (id == null) return;
        discussionMapper.updateClosedStatus(id, isClosed);
    }

    @Override
    public int countDiscussionsByCourse(Long courseId) {
        return discussionMapper.countByCourseId(courseId);
    }

    @Override
    public int countDiscussionsByClass(Long classId) {
        return discussionMapper.countByClassId(classId);
    }
}
