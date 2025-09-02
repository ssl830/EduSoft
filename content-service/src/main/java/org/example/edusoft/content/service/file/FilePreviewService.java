package org.example.edusoft.content.service.file;

public interface FilePreviewService {
    
    /**
     * 获取文件预览信息
     */
    String getPreviewUrl(Long fileId);
}
