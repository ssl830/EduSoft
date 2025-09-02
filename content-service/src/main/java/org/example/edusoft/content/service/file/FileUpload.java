package org.example.edusoft.content.service.file;

import org.springframework.web.multipart.MultipartFile;
import org.example.edusoft.content.common.Result;

public interface FileUpload {
    
    /**
     * 上传文件
     */
    Result<?> uploadFile(
        MultipartFile file, 
        String title, 
        Long courseId, 
        Long sectionId, 
        String visibility, 
        Long uploaderId, 
        String type, 
        Boolean uploadToKnowledgeBase
    );
}
