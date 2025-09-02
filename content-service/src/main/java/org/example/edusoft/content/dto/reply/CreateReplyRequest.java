package org.example.edusoft.content.dto.reply;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReplyRequest {
    
    @NotNull(message = "讨论ID不能为空")
    private Long discussionId;
    
    private Long parentReplyId;
    
    @NotBlank(message = "回复内容不能为空")
    private String content;
}
