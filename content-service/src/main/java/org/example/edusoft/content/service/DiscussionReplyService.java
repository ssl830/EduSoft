package org.example.edusoft.content.service;

import org.example.edusoft.content.entity.DiscussionReply;

import java.util.List;

public interface DiscussionReplyService {
    
    DiscussionReply createReply(DiscussionReply reply);
    
    DiscussionReply getReplyById(Long id);
    
    List<DiscussionReply> getRepliesByDiscussionId(Long discussionId);
    
    List<DiscussionReply> getRepliesByUserId(Long userId);
    
    List<DiscussionReply> getRepliesByParentReplyId(Long parentReplyId);
    
    DiscussionReply updateReply(DiscussionReply reply);
    
    void deleteReply(Long id);
    
    void markAsTeacherReply(Long id);
}

