package org.example.edusoft.content.service.file;

import org.example.edusoft.content.entity.file.FileAccessDTO;

public interface FileAccessService {
    
    /**
     * 获取下载URL
     */
    FileAccessDTO getDownloadUrl(Long fileId);
    
    /**
     * 获取预览URL
     */
    FileAccessDTO getPreviewUrl(Long fileId);
}
