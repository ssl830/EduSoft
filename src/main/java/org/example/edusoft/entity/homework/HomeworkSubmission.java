package org.example.edusoft.entity.homework;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 作业提交实体类
 */
@Data
public class HomeworkSubmission {
    /**
     * 提交ID
     */
    private Long id;

    /**
     * 作业ID
     */
    private Long homeworkId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 提交文件URL（阿里云OSS）
     */
    private String fileUrl;

    /**
     * 对象存储中的文件路径
     * 格式：homework/submission/{homeworkId}/{studentId}_{fileName}
     */
    private String objectName;

    /**
     * 提交时间
     */
    private LocalDateTime submittedAt;
}
