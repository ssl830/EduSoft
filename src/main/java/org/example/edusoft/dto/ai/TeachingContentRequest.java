package org.example.edusoft.dto.ai;

import lombok.Data;

@Data
public class TeachingContentRequest {
    /**
     * 课程大纲内容
     */
    private String courseOutline;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 预期课时数
     */
    private Integer expectedHours;
} 