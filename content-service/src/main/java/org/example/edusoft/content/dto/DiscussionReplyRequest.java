package org.example.edusoft.content.dto;

import lombok.Data;

@Data
public class DiscussionReplyRequest {
    private Long discussionId;
    private Long userId;
    private String userNum;
    private String content;
    private Long parentReplyId;
    private Boolean isTeacherReply;
}

