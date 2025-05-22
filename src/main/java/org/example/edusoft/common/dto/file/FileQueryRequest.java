package org.example.edusoft.common.dto.file;
import lombok.Data;

@Data
public class FileQueryRequest {
    private Long chapter;     // 章节
    private String type;        // 类型（PDF/PPT/VIDEO/CODE/OTHER）
    private String title;       // 文件标题（模糊匹配）
    private Long courseId;    // 课程ID
    private Long userId;        // 用户ID
}