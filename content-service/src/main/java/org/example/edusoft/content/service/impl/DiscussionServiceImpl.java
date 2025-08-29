package org.example.edusoft.content.service.impl;

import org.example.edusoft.content.entity.Discussion;
import org.example.edusoft.content.mapper.DiscussionMapper;
import org.example.edusoft.content.service.DiscussionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussionServiceImpl implements DiscussionService {

    @Autowired
    private DiscussionMapper discussionMapper;

    @Override
    public Discussion createDiscussion(Discussion discussion) {
        discussionMapper.insert(discussion);
        return discussion;
    }

    @Override
    public Discussion getDiscussionById(Long id) {
        return discussionMapper.findById(id);
    }

    @Override
    public List<Discussion> getDiscussionsByCreatorId(Long creatorId) {
        return discussionMapper.findByCreatorId(creatorId);
    }

    @Override
    public List<Discussion> getDiscussionsByCourseId(Long courseId) {
        return discussionMapper.findByCourseId(courseId);
    }

    @Override
    public List<Discussion> getDiscussionsByClassId(Long classId) {
        return discussionMapper.findByClassId(classId);
    }

    @Override
    public List<Discussion> getDiscussionsByType(String type) {
        return discussionMapper.findByType(type);
    }

    @Override
    public List<Discussion> getAllDiscussions() {
        return discussionMapper.findAll();
    }

    @Override
    public Discussion updateDiscussion(Discussion discussion) {
        discussionMapper.update(discussion);
        return discussion;
    }

    @Override
    public void closeDiscussion(Long id) {
        discussionMapper.closeById(id);
    }

    @Override
    public void pinDiscussion(Long id) {
        discussionMapper.pinById(id);
    }

    @Override
    public void unpinDiscussion(Long id) {
        discussionMapper.unpinById(id);
    }

    @Override
    public void incrementViewCount(Long id) {
        discussionMapper.incrementViewCount(id);
    }

    @Override
    public void incrementReplyCount(Long id) {
        discussionMapper.incrementReplyCount(id);
    }
}

