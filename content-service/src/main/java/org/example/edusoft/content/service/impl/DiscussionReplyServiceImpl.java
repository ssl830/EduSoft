package org.example.edusoft.content.service.impl;

import org.example.edusoft.content.entity.DiscussionReply;
import org.example.edusoft.content.mapper.DiscussionReplyMapper;
import org.example.edusoft.content.service.DiscussionReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussionReplyServiceImpl implements DiscussionReplyService {

    @Autowired
    private DiscussionReplyMapper discussionReplyMapper;

    @Override
    public DiscussionReply createReply(DiscussionReply reply) {
        discussionReplyMapper.insert(reply);
        return reply;
    }

    @Override
    public DiscussionReply getReplyById(Long id) {
        return discussionReplyMapper.findById(id);
    }

    @Override
    public List<DiscussionReply> getRepliesByDiscussionId(Long discussionId) {
        return discussionReplyMapper.findByDiscussionId(discussionId);
    }

    @Override
    public List<DiscussionReply> getRepliesByUserId(Long userId) {
        return discussionReplyMapper.findByUserId(userId);
    }

    @Override
    public List<DiscussionReply> getRepliesByParentReplyId(Long parentReplyId) {
        return discussionReplyMapper.findByParentReplyId(parentReplyId);
    }

    @Override
    public DiscussionReply updateReply(DiscussionReply reply) {
        discussionReplyMapper.update(reply);
        return reply;
    }

    @Override
    public void deleteReply(Long id) {
        discussionReplyMapper.deleteById(id);
    }

    @Override
    public void markAsTeacherReply(Long id) {
        discussionReplyMapper.markAsTeacherReply(id);
    }
}

