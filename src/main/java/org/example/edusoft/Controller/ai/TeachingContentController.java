package org.example.edusoft.controller.ai;

import org.example.edusoft.dto.ai.TeachingContentRequest;
import org.example.edusoft.dto.ai.TeachingContentResponse;
import org.example.edusoft.service.ai.TeachingContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/teaching")
public class TeachingContentController {
    
    @Autowired
    private TeachingContentService teachingContentService;
    
    /**
     * 根据课程大纲生成教学内容
     */
    @PostMapping("/generate")
    public TeachingContentResponse generateTeachingContent(@RequestBody TeachingContentRequest request) {
        return teachingContentService.generateTeachingContent(request);
    }
} 