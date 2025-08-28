package org.example.edusoft.dto.ai;

import lombok.Data;
import java.util.List;

@Data
public class TeachingContentResponse {
    /**
     * 教学内容列表，每个元素代表一个课时
     */
    private List<LessonContent> lessons;
    
    /**
     * 总课时数
     */
    private Integer totalHours;
    
    /**
     * 建议的时间分配
     */
    private String timeDistribution;
    
    /**
     * 教学建议
     */
    private String teachingAdvice;

    @Data
    public static class LessonContent {
        /**
         * 课时标题
         */
        private String title;
        
        /**
         * 知识点讲解内容
         */
        private String content;
        
        /**
         * 实训练习内容
         */
        private String practiceContent;
        
        /**
         * 教学指导建议
         */
        private String teachingGuidance;
        
        /**
         * 建议课时数
         */
        private Integer suggestedHours;
        
        /**
         * 相关知识点来源
         */
        private List<String> knowledgeSources;
    }
} 