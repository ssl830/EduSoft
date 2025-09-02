package org.example.edusoft.content.dto.reply;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscussionReplyDTO {
    
    private Long id;
    private Long discussionId;
    private Long parentReplyId;
    private Long creatorId;
    private String creatorName;
    private String content;
    private Integer likeCount;
    private Boolean isLiked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
}
