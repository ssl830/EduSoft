package org.example.edusoft.dto.homework;

import lombok.Data;
import lombok.Builder;

/**
 * 作业数据传输对象
 */
@Data
@Builder
public class HomeworkDTO {
    /**
     * 作业ID
     */
    private Long homeworkId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 作业标题
     */
    private String title;

    /**
     * 作业描述
     */
    private String description;

    /**
     * 带签名的文件访问URL
     */
    private String fileUrl;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 截止时间
     */
    private String endTime;
} 