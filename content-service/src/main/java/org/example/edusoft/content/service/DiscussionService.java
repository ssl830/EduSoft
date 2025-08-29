package org.example.edusoft.content.service;

import org.example.edusoft.content.entity.Discussion;

import java.util.List;

public interface DiscussionService {
    
    Discussion createDiscussion(Discussion discussion);
    
    Discussion getDiscussionById(Long id);
    
    List<Discussion> getDiscussionsByCreatorId(Long creatorId);
    
    List<Discussion> getDiscussionsByCourseId(Long courseId);
    
    List<Discussion> getDiscussionsByClassId(Long classId);
    
    List<Discussion> getDiscussionsByType(String type);
    
    List<Discussion> getAllDiscussions();
    
    Discussion updateDiscussion(Discussion discussion);
    
    void closeDiscussion(Long id);
    
    void pinDiscussion(Long id);
    
    void unpinDiscussion(Long id);
    
    void incrementViewCount(Long id);
    
    void incrementReplyCount(Long id);
}
