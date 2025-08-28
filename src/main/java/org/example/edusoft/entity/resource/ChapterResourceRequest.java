package org.example.edusoft.entity.resource;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 章节资源请求DTO
 */
@Data
public class ChapterResourceRequest {
    /**
     * 学生ID
     */
    @NotNull(message = "学生ID不能为空")
    @JsonProperty("studentId")
    private Long studentId;

    /**
     * 章节ID（-1表示不筛选章节）
     */
    @NotNull(message = "章节ID不能为空")
    @JsonProperty("chapterId")
    private Long chapterId;
} 